package edu.jeremiah.sommerfeld.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.jeremiah.sommerfeld.domain.Card;
import edu.jeremiah.sommerfeld.repository.CardRepository;
import edu.jeremiah.sommerfeld.repository.search.CardSearchRepository;
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
 * REST controller for managing Card.
 */
@RestController
@RequestMapping("/api")
public class CardResource {

    private final Logger log = LoggerFactory.getLogger(CardResource.class);
        
    @Inject
    private CardRepository cardRepository;
    
    @Inject
    private CardSearchRepository cardSearchRepository;
    
    /**
     * POST  /cards : Create a new card.
     *
     * @param card the card to create
     * @return the ResponseEntity with status 201 (Created) and with body the new card, or with status 400 (Bad Request) if the card has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cards",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Card> createCard(@RequestBody Card card) throws URISyntaxException {
        log.debug("REST request to save Card : {}", card);
        if (card.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("card", "idexists", "A new card cannot already have an ID")).body(null);
        }
        Card result = cardRepository.save(card);
        cardSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/cards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("card", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cards : Updates an existing card.
     *
     * @param card the card to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated card,
     * or with status 400 (Bad Request) if the card is not valid,
     * or with status 500 (Internal Server Error) if the card couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cards",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Card> updateCard(@RequestBody Card card) throws URISyntaxException {
        log.debug("REST request to update Card : {}", card);
        if (card.getId() == null) {
            return createCard(card);
        }
        Card result = cardRepository.save(card);
        cardSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("card", card.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cards : get all the cards.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cards in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/cards",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Card>> getAllCards(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Cards");
        Page<Card> page = cardRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cards");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /cards/:id : get the "id" card.
     *
     * @param id the id of the card to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the card, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/cards/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Card> getCard(@PathVariable Long id) {
        log.debug("REST request to get Card : {}", id);
        Card card = cardRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(card)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cards/:id : delete the "id" card.
     *
     * @param id the id of the card to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/cards/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        log.debug("REST request to delete Card : {}", id);
        cardRepository.delete(id);
        cardSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("card", id.toString())).build();
    }

    /**
     * SEARCH  /_search/cards?query=:query : search for the card corresponding
     * to the query.
     *
     * @param query the query of the card search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/cards",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Card>> searchCards(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Cards for query {}", query);
        Page<Card> page = cardSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/cards");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
