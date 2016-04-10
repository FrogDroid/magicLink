package edu.jeremiah.sommerfeld.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.jeremiah.sommerfeld.domain.Color;
import edu.jeremiah.sommerfeld.repository.ColorRepository;
import edu.jeremiah.sommerfeld.repository.search.ColorSearchRepository;
import edu.jeremiah.sommerfeld.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Color.
 */
@RestController
@RequestMapping("/api")
public class ColorResource {

    private final Logger log = LoggerFactory.getLogger(ColorResource.class);
        
    @Inject
    private ColorRepository colorRepository;
    
    @Inject
    private ColorSearchRepository colorSearchRepository;
    
    /**
     * POST  /colors : Create a new color.
     *
     * @param color the color to create
     * @return the ResponseEntity with status 201 (Created) and with body the new color, or with status 400 (Bad Request) if the color has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/colors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Color> createColor(@Valid @RequestBody Color color) throws URISyntaxException {
        log.debug("REST request to save Color : {}", color);
        if (color.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("color", "idexists", "A new color cannot already have an ID")).body(null);
        }
        Color result = colorRepository.save(color);
        colorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/colors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("color", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /colors : Updates an existing color.
     *
     * @param color the color to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated color,
     * or with status 400 (Bad Request) if the color is not valid,
     * or with status 500 (Internal Server Error) if the color couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/colors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Color> updateColor(@Valid @RequestBody Color color) throws URISyntaxException {
        log.debug("REST request to update Color : {}", color);
        if (color.getId() == null) {
            return createColor(color);
        }
        Color result = colorRepository.save(color);
        colorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("color", color.getId().toString()))
            .body(result);
    }

    /**
     * GET  /colors : get all the colors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of colors in body
     */
    @RequestMapping(value = "/colors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Color> getAllColors() {
        log.debug("REST request to get all Colors");
        List<Color> colors = colorRepository.findAll();
        return colors;
    }

    /**
     * GET  /colors/:id : get the "id" color.
     *
     * @param id the id of the color to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the color, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/colors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Color> getColor(@PathVariable Long id) {
        log.debug("REST request to get Color : {}", id);
        Color color = colorRepository.findOne(id);
        return Optional.ofNullable(color)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /colors/:id : delete the "id" color.
     *
     * @param id the id of the color to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/colors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteColor(@PathVariable Long id) {
        log.debug("REST request to delete Color : {}", id);
        colorRepository.delete(id);
        colorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("color", id.toString())).build();
    }

    /**
     * SEARCH  /_search/colors?query=:query : search for the color corresponding
     * to the query.
     *
     * @param query the query of the color search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/colors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Color> searchColors(@RequestParam String query) {
        log.debug("REST request to search Colors for query {}", query);
        return StreamSupport
            .stream(colorSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
