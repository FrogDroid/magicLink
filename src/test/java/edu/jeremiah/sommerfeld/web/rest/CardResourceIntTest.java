package edu.jeremiah.sommerfeld.web.rest;

import edu.jeremiah.sommerfeld.MagicLinkApp;
import edu.jeremiah.sommerfeld.domain.Card;
import edu.jeremiah.sommerfeld.repository.CardRepository;
import edu.jeremiah.sommerfeld.repository.search.CardSearchRepository;

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
 * Test class for the CardResource REST controller.
 *
 * @see CardResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MagicLinkApp.class)
@WebAppConfiguration
@IntegrationTest
public class CardResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_MANA_COST = "AAAAA";
    private static final String UPDATED_MANA_COST = "BBBBB";

    private static final Long DEFAULT_CMC = 1L;
    private static final Long UPDATED_CMC = 2L;
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_RARITY = "AAAAA";
    private static final String UPDATED_RARITY = "BBBBB";
    private static final String DEFAULT_TEXT = "AAAAA";
    private static final String UPDATED_TEXT = "BBBBB";
    private static final String DEFAULT_FLAVOR = "AAAAA";
    private static final String UPDATED_FLAVOR = "BBBBB";
    private static final String DEFAULT_ARTIST = "AAAAA";
    private static final String UPDATED_ARTIST = "BBBBB";
    private static final String DEFAULT_NUMBER = "AAAAA";
    private static final String UPDATED_NUMBER = "BBBBB";

    private static final Long DEFAULT_POWER = 1L;
    private static final Long UPDATED_POWER = 2L;

    private static final Long DEFAULT_TOUGHNESS = 1L;
    private static final Long UPDATED_TOUGHNESS = 2L;
    private static final String DEFAULT_LAYOUT = "AAAAA";
    private static final String UPDATED_LAYOUT = "BBBBB";
    private static final String DEFAULT_MULTIVERSEID = "AAAAA";
    private static final String UPDATED_MULTIVERSEID = "BBBBB";
    private static final String DEFAULT_IMAGE_NAME = "AAAAA";
    private static final String UPDATED_IMAGE_NAME = "BBBBB";

    @Inject
    private CardRepository cardRepository;

    @Inject
    private CardSearchRepository cardSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCardMockMvc;

    private Card card;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CardResource cardResource = new CardResource();
        ReflectionTestUtils.setField(cardResource, "cardSearchRepository", cardSearchRepository);
        ReflectionTestUtils.setField(cardResource, "cardRepository", cardRepository);
        this.restCardMockMvc = MockMvcBuilders.standaloneSetup(cardResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        cardSearchRepository.deleteAll();
        card = new Card();
        card.setName(DEFAULT_NAME);
        card.setManaCost(DEFAULT_MANA_COST);
        card.setCmc(DEFAULT_CMC);
        card.setType(DEFAULT_TYPE);
        card.setRarity(DEFAULT_RARITY);
        card.setText(DEFAULT_TEXT);
        card.setFlavor(DEFAULT_FLAVOR);
        card.setArtist(DEFAULT_ARTIST);
        card.setNumber(DEFAULT_NUMBER);
        card.setPower(DEFAULT_POWER);
        card.setToughness(DEFAULT_TOUGHNESS);
        card.setLayout(DEFAULT_LAYOUT);
        card.setMultiverseid(DEFAULT_MULTIVERSEID);
        card.setImageName(DEFAULT_IMAGE_NAME);
    }

    @Test
    @Transactional
    public void createCard() throws Exception {
        int databaseSizeBeforeCreate = cardRepository.findAll().size();

        // Create the Card

        restCardMockMvc.perform(post("/api/cards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(card)))
                .andExpect(status().isCreated());

        // Validate the Card in the database
        List<Card> cards = cardRepository.findAll();
        assertThat(cards).hasSize(databaseSizeBeforeCreate + 1);
        Card testCard = cards.get(cards.size() - 1);
        assertThat(testCard.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCard.getManaCost()).isEqualTo(DEFAULT_MANA_COST);
        assertThat(testCard.getCmc()).isEqualTo(DEFAULT_CMC);
        assertThat(testCard.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCard.getRarity()).isEqualTo(DEFAULT_RARITY);
        assertThat(testCard.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testCard.getFlavor()).isEqualTo(DEFAULT_FLAVOR);
        assertThat(testCard.getArtist()).isEqualTo(DEFAULT_ARTIST);
        assertThat(testCard.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testCard.getPower()).isEqualTo(DEFAULT_POWER);
        assertThat(testCard.getToughness()).isEqualTo(DEFAULT_TOUGHNESS);
        assertThat(testCard.getLayout()).isEqualTo(DEFAULT_LAYOUT);
        assertThat(testCard.getMultiverseid()).isEqualTo(DEFAULT_MULTIVERSEID);
        assertThat(testCard.getImageName()).isEqualTo(DEFAULT_IMAGE_NAME);

        // Validate the Card in ElasticSearch
        Card cardEs = cardSearchRepository.findOne(testCard.getId());
        assertThat(cardEs).isEqualToComparingFieldByField(testCard);
    }

    @Test
    @Transactional
    public void getAllCards() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cards
        restCardMockMvc.perform(get("/api/cards?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(card.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].manaCost").value(hasItem(DEFAULT_MANA_COST.toString())))
                .andExpect(jsonPath("$.[*].cmc").value(hasItem(DEFAULT_CMC.intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].rarity").value(hasItem(DEFAULT_RARITY.toString())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
                .andExpect(jsonPath("$.[*].flavor").value(hasItem(DEFAULT_FLAVOR.toString())))
                .andExpect(jsonPath("$.[*].artist").value(hasItem(DEFAULT_ARTIST.toString())))
                .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].power").value(hasItem(DEFAULT_POWER.intValue())))
                .andExpect(jsonPath("$.[*].toughness").value(hasItem(DEFAULT_TOUGHNESS.intValue())))
                .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT.toString())))
                .andExpect(jsonPath("$.[*].multiverseid").value(hasItem(DEFAULT_MULTIVERSEID.toString())))
                .andExpect(jsonPath("$.[*].imageName").value(hasItem(DEFAULT_IMAGE_NAME.toString())));
    }

    @Test
    @Transactional
    public void getCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get the card
        restCardMockMvc.perform(get("/api/cards/{id}", card.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(card.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.manaCost").value(DEFAULT_MANA_COST.toString()))
            .andExpect(jsonPath("$.cmc").value(DEFAULT_CMC.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.rarity").value(DEFAULT_RARITY.toString()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.flavor").value(DEFAULT_FLAVOR.toString()))
            .andExpect(jsonPath("$.artist").value(DEFAULT_ARTIST.toString()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER.toString()))
            .andExpect(jsonPath("$.power").value(DEFAULT_POWER.intValue()))
            .andExpect(jsonPath("$.toughness").value(DEFAULT_TOUGHNESS.intValue()))
            .andExpect(jsonPath("$.layout").value(DEFAULT_LAYOUT.toString()))
            .andExpect(jsonPath("$.multiverseid").value(DEFAULT_MULTIVERSEID.toString()))
            .andExpect(jsonPath("$.imageName").value(DEFAULT_IMAGE_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCard() throws Exception {
        // Get the card
        restCardMockMvc.perform(get("/api/cards/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);
        cardSearchRepository.save(card);
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();

        // Update the card
        Card updatedCard = new Card();
        updatedCard.setId(card.getId());
        updatedCard.setName(UPDATED_NAME);
        updatedCard.setManaCost(UPDATED_MANA_COST);
        updatedCard.setCmc(UPDATED_CMC);
        updatedCard.setType(UPDATED_TYPE);
        updatedCard.setRarity(UPDATED_RARITY);
        updatedCard.setText(UPDATED_TEXT);
        updatedCard.setFlavor(UPDATED_FLAVOR);
        updatedCard.setArtist(UPDATED_ARTIST);
        updatedCard.setNumber(UPDATED_NUMBER);
        updatedCard.setPower(UPDATED_POWER);
        updatedCard.setToughness(UPDATED_TOUGHNESS);
        updatedCard.setLayout(UPDATED_LAYOUT);
        updatedCard.setMultiverseid(UPDATED_MULTIVERSEID);
        updatedCard.setImageName(UPDATED_IMAGE_NAME);

        restCardMockMvc.perform(put("/api/cards")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCard)))
                .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cards = cardRepository.findAll();
        assertThat(cards).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cards.get(cards.size() - 1);
        assertThat(testCard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCard.getManaCost()).isEqualTo(UPDATED_MANA_COST);
        assertThat(testCard.getCmc()).isEqualTo(UPDATED_CMC);
        assertThat(testCard.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCard.getRarity()).isEqualTo(UPDATED_RARITY);
        assertThat(testCard.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testCard.getFlavor()).isEqualTo(UPDATED_FLAVOR);
        assertThat(testCard.getArtist()).isEqualTo(UPDATED_ARTIST);
        assertThat(testCard.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testCard.getPower()).isEqualTo(UPDATED_POWER);
        assertThat(testCard.getToughness()).isEqualTo(UPDATED_TOUGHNESS);
        assertThat(testCard.getLayout()).isEqualTo(UPDATED_LAYOUT);
        assertThat(testCard.getMultiverseid()).isEqualTo(UPDATED_MULTIVERSEID);
        assertThat(testCard.getImageName()).isEqualTo(UPDATED_IMAGE_NAME);

        // Validate the Card in ElasticSearch
        Card cardEs = cardSearchRepository.findOne(testCard.getId());
        assertThat(cardEs).isEqualToComparingFieldByField(testCard);
    }

    @Test
    @Transactional
    public void deleteCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);
        cardSearchRepository.save(card);
        int databaseSizeBeforeDelete = cardRepository.findAll().size();

        // Get the card
        restCardMockMvc.perform(delete("/api/cards/{id}", card.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean cardExistsInEs = cardSearchRepository.exists(card.getId());
        assertThat(cardExistsInEs).isFalse();

        // Validate the database is empty
        List<Card> cards = cardRepository.findAll();
        assertThat(cards).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);
        cardSearchRepository.save(card);

        // Search the card
        restCardMockMvc.perform(get("/api/_search/cards?query=id:" + card.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(card.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].manaCost").value(hasItem(DEFAULT_MANA_COST.toString())))
            .andExpect(jsonPath("$.[*].cmc").value(hasItem(DEFAULT_CMC.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].rarity").value(hasItem(DEFAULT_RARITY.toString())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].flavor").value(hasItem(DEFAULT_FLAVOR.toString())))
            .andExpect(jsonPath("$.[*].artist").value(hasItem(DEFAULT_ARTIST.toString())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].power").value(hasItem(DEFAULT_POWER.intValue())))
            .andExpect(jsonPath("$.[*].toughness").value(hasItem(DEFAULT_TOUGHNESS.intValue())))
            .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT.toString())))
            .andExpect(jsonPath("$.[*].multiverseid").value(hasItem(DEFAULT_MULTIVERSEID.toString())))
            .andExpect(jsonPath("$.[*].imageName").value(hasItem(DEFAULT_IMAGE_NAME.toString())));
    }
}
