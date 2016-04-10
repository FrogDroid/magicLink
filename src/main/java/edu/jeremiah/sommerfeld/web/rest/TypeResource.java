package edu.jeremiah.sommerfeld.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.jeremiah.sommerfeld.domain.Type;
import edu.jeremiah.sommerfeld.repository.TypeRepository;
import edu.jeremiah.sommerfeld.repository.search.TypeSearchRepository;
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
 * REST controller for managing Type.
 */
@RestController
@RequestMapping("/api")
public class TypeResource {

    private final Logger log = LoggerFactory.getLogger(TypeResource.class);
        
    @Inject
    private TypeRepository typeRepository;
    
    @Inject
    private TypeSearchRepository typeSearchRepository;
    
    /**
     * POST  /types : Create a new type.
     *
     * @param type the type to create
     * @return the ResponseEntity with status 201 (Created) and with body the new type, or with status 400 (Bad Request) if the type has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Type> createType(@RequestBody Type type) throws URISyntaxException {
        log.debug("REST request to save Type : {}", type);
        if (type.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("type", "idexists", "A new type cannot already have an ID")).body(null);
        }
        Type result = typeRepository.save(type);
        typeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("type", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /types : Updates an existing type.
     *
     * @param type the type to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated type,
     * or with status 400 (Bad Request) if the type is not valid,
     * or with status 500 (Internal Server Error) if the type couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Type> updateType(@RequestBody Type type) throws URISyntaxException {
        log.debug("REST request to update Type : {}", type);
        if (type.getId() == null) {
            return createType(type);
        }
        Type result = typeRepository.save(type);
        typeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("type", type.getId().toString()))
            .body(result);
    }

    /**
     * GET  /types : get all the types.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of types in body
     */
    @RequestMapping(value = "/types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Type> getAllTypes() {
        log.debug("REST request to get all Types");
        List<Type> types = typeRepository.findAll();
        return types;
    }

    /**
     * GET  /types/:id : get the "id" type.
     *
     * @param id the id of the type to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the type, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Type> getType(@PathVariable Long id) {
        log.debug("REST request to get Type : {}", id);
        Type type = typeRepository.findOne(id);
        return Optional.ofNullable(type)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /types/:id : delete the "id" type.
     *
     * @param id the id of the type to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        log.debug("REST request to delete Type : {}", id);
        typeRepository.delete(id);
        typeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("type", id.toString())).build();
    }

    /**
     * SEARCH  /_search/types?query=:query : search for the type corresponding
     * to the query.
     *
     * @param query the query of the type search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Type> searchTypes(@RequestParam String query) {
        log.debug("REST request to search Types for query {}", query);
        return StreamSupport
            .stream(typeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
