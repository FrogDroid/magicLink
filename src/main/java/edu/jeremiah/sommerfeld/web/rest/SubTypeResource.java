package edu.jeremiah.sommerfeld.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.jeremiah.sommerfeld.domain.SubType;
import edu.jeremiah.sommerfeld.repository.SubTypeRepository;
import edu.jeremiah.sommerfeld.repository.search.SubTypeSearchRepository;
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
 * REST controller for managing SubType.
 */
@RestController
@RequestMapping("/api")
public class SubTypeResource {

    private final Logger log = LoggerFactory.getLogger(SubTypeResource.class);
        
    @Inject
    private SubTypeRepository subTypeRepository;
    
    @Inject
    private SubTypeSearchRepository subTypeSearchRepository;
    
    /**
     * POST  /sub-types : Create a new subType.
     *
     * @param subType the subType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new subType, or with status 400 (Bad Request) if the subType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sub-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubType> createSubType(@RequestBody SubType subType) throws URISyntaxException {
        log.debug("REST request to save SubType : {}", subType);
        if (subType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("subType", "idexists", "A new subType cannot already have an ID")).body(null);
        }
        SubType result = subTypeRepository.save(subType);
        subTypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/sub-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("subType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sub-types : Updates an existing subType.
     *
     * @param subType the subType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated subType,
     * or with status 400 (Bad Request) if the subType is not valid,
     * or with status 500 (Internal Server Error) if the subType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sub-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubType> updateSubType(@RequestBody SubType subType) throws URISyntaxException {
        log.debug("REST request to update SubType : {}", subType);
        if (subType.getId() == null) {
            return createSubType(subType);
        }
        SubType result = subTypeRepository.save(subType);
        subTypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("subType", subType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sub-types : get all the subTypes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of subTypes in body
     */
    @RequestMapping(value = "/sub-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SubType> getAllSubTypes() {
        log.debug("REST request to get all SubTypes");
        List<SubType> subTypes = subTypeRepository.findAll();
        return subTypes;
    }

    /**
     * GET  /sub-types/:id : get the "id" subType.
     *
     * @param id the id of the subType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the subType, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sub-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubType> getSubType(@PathVariable Long id) {
        log.debug("REST request to get SubType : {}", id);
        SubType subType = subTypeRepository.findOne(id);
        return Optional.ofNullable(subType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sub-types/:id : delete the "id" subType.
     *
     * @param id the id of the subType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sub-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSubType(@PathVariable Long id) {
        log.debug("REST request to delete SubType : {}", id);
        subTypeRepository.delete(id);
        subTypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sub-types?query=:query : search for the subType corresponding
     * to the query.
     *
     * @param query the query of the subType search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/sub-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SubType> searchSubTypes(@RequestParam String query) {
        log.debug("REST request to search SubTypes for query {}", query);
        return StreamSupport
            .stream(subTypeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
