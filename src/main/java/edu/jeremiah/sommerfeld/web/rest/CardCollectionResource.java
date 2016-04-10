package edu.jeremiah.sommerfeld.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.jeremiah.sommerfeld.domain.CardCollection;
import edu.jeremiah.sommerfeld.repository.CardCollectionRepository;
import edu.jeremiah.sommerfeld.repository.search.CardCollectionSearchRepository;
import edu.jeremiah.sommerfeld.web.rest.util.HeaderUtil;
import edu.jeremiah.sommerfeld.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing CardCollection.
 */
@RestController
@RequestMapping("/api")
public class CardCollectionResource {

    private final Logger log = LoggerFactory.getLogger(CardCollectionResource.class);
        
    @Inject
    private CardCollectionRepository cardCollectionRepository;
    
    @Inject
    private CardCollectionSearchRepository cardCollectionSearchRepository;
    
    /**
     * POST  /card-collections : Create a new cardCollection.
     *
     * @param cardCollection the cardCollection to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cardCollection, or with status 400 (Bad Request) if the cardCollection has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/card-collections",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CardCollection> createCardCollection(@RequestBody CardCollection cardCollection) throws URISyntaxException {
        log.debug("REST request to save CardCollection : {}", cardCollection);
        if (cardCollection.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cardCollection", "idexists", "A new cardCollection cannot already have an ID")).body(null);
        }
        CardCollection result = cardCollectionRepository.save(cardCollection);
        cardCollectionSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/card-collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cardCollection", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /card-collections : Updates an existing cardCollection.
     *
     * @param cardCollection the cardCollection to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cardCollection,
     * or with status 400 (Bad Request) if the cardCollection is not valid,
     * or with status 500 (Internal Server Error) if the cardCollection couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/card-collections",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CardCollection> updateCardCollection(@RequestBody CardCollection cardCollection) throws URISyntaxException {
        log.debug("REST request to update CardCollection : {}", cardCollection);
        if (cardCollection.getId() == null) {
            return createCardCollection(cardCollection);
        }
        CardCollection result = cardCollectionRepository.save(cardCollection);
        cardCollectionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cardCollection", cardCollection.getId().toString()))
            .body(result);
    }

    /**
     * GET  /card-collections : get all the cardCollections.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cardCollections in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/card-collections",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CardCollection>> getAllCardCollections(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of CardCollections");
        Page<CardCollection> page = cardCollectionRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/card-collections");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /card-collections/:id : get the "id" cardCollection.
     *
     * @param id the id of the cardCollection to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cardCollection, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/card-collections/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CardCollection> getCardCollection(@PathVariable Long id) {
        log.debug("REST request to get CardCollection : {}", id);
        CardCollection cardCollection = cardCollectionRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(cardCollection)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /card-collections/:id : delete the "id" cardCollection.
     *
     * @param id the id of the cardCollection to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/card-collections/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCardCollection(@PathVariable Long id) {
        log.debug("REST request to delete CardCollection : {}", id);
        cardCollectionRepository.delete(id);
        cardCollectionSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cardCollection", id.toString())).build();
    }

    /**
     * SEARCH  /_search/card-collections?query=:query : search for the cardCollection corresponding
     * to the query.
     *
     * @param query the query of the cardCollection search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/card-collections",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CardCollection>> searchCardCollections(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of CardCollections for query {}", query);
        Page<CardCollection> page = cardCollectionSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/card-collections");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
