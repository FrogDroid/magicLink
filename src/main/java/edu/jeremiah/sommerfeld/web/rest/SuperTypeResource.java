package edu.jeremiah.sommerfeld.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.jeremiah.sommerfeld.domain.SuperType;
import edu.jeremiah.sommerfeld.repository.SuperTypeRepository;
import edu.jeremiah.sommerfeld.repository.search.SuperTypeSearchRepository;
import edu.jeremiah.sommerfeld.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing SuperType.
 */
@RestController
@RequestMapping("/api")
public class SuperTypeResource {

    private final Logger log = LoggerFactory.getLogger(SuperTypeResource.class);
        
    @Inject
    private SuperTypeRepository superTypeRepository;
    
    @Inject
    private SuperTypeSearchRepository superTypeSearchRepository;
    
    /**
     * POST  /super-types : Create a new superType.
     *
     * @param superType the superType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new superType, or with status 400 (Bad Request) if the superType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/super-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SuperType> createSuperType(@RequestBody SuperType superType) throws URISyntaxException {
        log.debug("REST request to save SuperType : {}", superType);
        if (superType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("superType", "idexists", "A new superType cannot already have an ID")).body(null);
        }
        SuperType result = superTypeRepository.save(superType);
        superTypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/super-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("superType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /super-types : Updates an existing superType.
     *
     * @param superType the superType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated superType,
     * or with status 400 (Bad Request) if the superType is not valid,
     * or with status 500 (Internal Server Error) if the superType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/super-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SuperType> updateSuperType(@RequestBody SuperType superType) throws URISyntaxException {
        log.debug("REST request to update SuperType : {}", superType);
        if (superType.getId() == null) {
            return createSuperType(superType);
        }
        SuperType result = superTypeRepository.save(superType);
        superTypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("superType", superType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /super-types : get all the superTypes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of superTypes in body
     */
    @RequestMapping(value = "/super-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SuperType> getAllSuperTypes() {
        log.debug("REST request to get all SuperTypes");
        List<SuperType> superTypes = superTypeRepository.findAll();
        return superTypes;
    }

    /**
     * GET  /super-types/:id : get the "id" superType.
     *
     * @param id the id of the superType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the superType, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/super-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SuperType> getSuperType(@PathVariable Long id) {
        log.debug("REST request to get SuperType : {}", id);
        SuperType superType = superTypeRepository.findOne(id);
        return Optional.ofNullable(superType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /super-types/:id : delete the "id" superType.
     *
     * @param id the id of the superType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/super-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSuperType(@PathVariable Long id) {
        log.debug("REST request to delete SuperType : {}", id);
        superTypeRepository.delete(id);
        superTypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("superType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/super-types?query=:query : search for the superType corresponding
     * to the query.
     *
     * @param query the query of the superType search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/super-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SuperType> searchSuperTypes(@RequestParam String query) {
        log.debug("REST request to search SuperTypes for query {}", query);
        return StreamSupport
            .stream(superTypeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
