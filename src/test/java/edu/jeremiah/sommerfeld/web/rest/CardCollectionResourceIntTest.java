package edu.jeremiah.sommerfeld.web.rest;

import edu.jeremiah.sommerfeld.MagicLinkApp;
import edu.jeremiah.sommerfeld.domain.CardCollection;
import edu.jeremiah.sommerfeld.repository.CardCollectionRepository;
import edu.jeremiah.sommerfeld.repository.search.CardCollectionSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CardCollectionResource REST controller.
 *
 * @see CardCollectionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MagicLinkApp.class)
@WebAppConfiguration
@IntegrationTest
public class CardCollectionResourceIntTest {


    @Inject
    private CardCollectionRepository cardCollectionRepository;

    @Inject
    private CardCollectionSearchRepository cardCollectionSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCardCollectionMockMvc;

    private CardCollection cardCollection;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CardCollectionResource cardCollectionResource = new CardCollectionResource();
        ReflectionTestUtils.setField(cardCollectionResource, "cardCollectionSearchRepository", cardCollectionSearchRepository);
        ReflectionTestUtils.setField(cardCollectionResource, "cardCollectionRepository", cardCollectionRepository);
        this.restCardCollectionMockMvc = MockMvcBuilders.standaloneSetup(cardCollectionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        cardCollectionSearchRepository.deleteAll();
        cardCollection = new CardCollection();
    }

    @Test
    @Transactional
    public void createCardCollection() throws Exception {
        int databaseSizeBeforeCreate = cardCollectionRepository.findAll().size();

        // Create the CardCollection

        restCardCollectionMockMvc.perform(post("/api/card-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cardCollection)))
                .andExpect(status().isCreated());

        // Validate the CardCollection in the database
        List<CardCollection> cardCollections = cardCollectionRepository.findAll();
        assertThat(cardCollections).hasSize(databaseSizeBeforeCreate + 1);
        CardCollection testCardCollection = cardCollections.get(cardCollections.size() - 1);

        // Validate the CardCollection in ElasticSearch
        CardCollection cardCollectionEs = cardCollectionSearchRepository.findOne(testCardCollection.getId());
        assertThat(cardCollectionEs).isEqualToComparingFieldByField(testCardCollection);
    }

    @Test
    @Transactional
    public void getAllCardCollections() throws Exception {
        // Initialize the database
        cardCollectionRepository.saveAndFlush(cardCollection);

        // Get all the cardCollections
        restCardCollectionMockMvc.perform(get("/api/card-collections?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cardCollection.getId().intValue())));
    }

    @Test
    @Transactional
    public void getCardCollection() throws Exception {
        // Initialize the database
        cardCollectionRepository.saveAndFlush(cardCollection);

        // Get the cardCollection
        restCardCollectionMockMvc.perform(get("/api/card-collections/{id}", cardCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cardCollection.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCardCollection() throws Exception {
        // Get the cardCollection
        restCardCollectionMockMvc.perform(get("/api/card-collections/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCardCollection() throws Exception {
        // Initialize the database
        cardCollectionRepository.saveAndFlush(cardCollection);
        cardCollectionSearchRepository.save(cardCollection);
        int databaseSizeBeforeUpdate = cardCollectionRepository.findAll().size();

        // Update the cardCollection
        CardCollection updatedCardCollection = new CardCollection();
        updatedCardCollection.setId(cardCollection.getId());

        restCardCollectionMockMvc.perform(put("/api/card-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCardCollection)))
                .andExpect(status().isOk());

        // Validate the CardCollection in the database
        List<CardCollection> cardCollections = cardCollectionRepository.findAll();
        assertThat(cardCollections).hasSize(databaseSizeBeforeUpdate);
        CardCollection testCardCollection = cardCollections.get(cardCollections.size() - 1);

        // Validate the CardCollection in ElasticSearch
        CardCollection cardCollectionEs = cardCollectionSearchRepository.findOne(testCardCollection.getId());
        assertThat(cardCollectionEs).isEqualToComparingFieldByField(testCardCollection);
    }

    @Test
    @Transactional
    public void deleteCardCollection() throws Exception {
        // Initialize the database
        cardCollectionRepository.saveAndFlush(cardCollection);
        cardCollectionSearchRepository.save(cardCollection);
        int databaseSizeBeforeDelete = cardCollectionRepository.findAll().size();

        // Get the cardCollection
        restCardCollectionMockMvc.perform(delete("/api/card-collections/{id}", cardCollection.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean cardCollectionExistsInEs = cardCollectionSearchRepository.exists(cardCollection.getId());
        assertThat(cardCollectionExistsInEs).isFalse();

        // Validate the database is empty
        List<CardCollection> cardCollections = cardCollectionRepository.findAll();
        assertThat(cardCollections).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCardCollection() throws Exception {
        // Initialize the database
        cardCollectionRepository.saveAndFlush(cardCollection);
        cardCollectionSearchRepository.save(cardCollection);

        // Search the cardCollection
        restCardCollectionMockMvc.perform(get("/api/_search/card-collections?query=id:" + cardCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cardCollection.getId().intValue())));
    }
}
