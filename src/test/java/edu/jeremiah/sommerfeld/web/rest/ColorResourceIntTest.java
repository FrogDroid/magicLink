package edu.jeremiah.sommerfeld.web.rest;

import edu.jeremiah.sommerfeld.MagicLinkApp;
import edu.jeremiah.sommerfeld.domain.Color;
import edu.jeremiah.sommerfeld.repository.ColorRepository;
import edu.jeremiah.sommerfeld.repository.search.ColorSearchRepository;

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
 * Test class for the ColorResource REST controller.
 *
 * @see ColorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MagicLinkApp.class)
@WebAppConfiguration
@IntegrationTest
public class ColorResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_CODE = "AAAAA";
    private static final String UPDATED_CODE = "BBBBB";

    @Inject
    private ColorRepository colorRepository;

    @Inject
    private ColorSearchRepository colorSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restColorMockMvc;

    private Color color;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ColorResource colorResource = new ColorResource();
        ReflectionTestUtils.setField(colorResource, "colorSearchRepository", colorSearchRepository);
        ReflectionTestUtils.setField(colorResource, "colorRepository", colorRepository);
        this.restColorMockMvc = MockMvcBuilders.standaloneSetup(colorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        colorSearchRepository.deleteAll();
        color = new Color();
        color.setName(DEFAULT_NAME);
        color.setCode(DEFAULT_CODE);
    }

    @Test
    @Transactional
    public void createColor() throws Exception {
        int databaseSizeBeforeCreate = colorRepository.findAll().size();

        // Create the Color

        restColorMockMvc.perform(post("/api/colors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(color)))
                .andExpect(status().isCreated());

        // Validate the Color in the database
        List<Color> colors = colorRepository.findAll();
        assertThat(colors).hasSize(databaseSizeBeforeCreate + 1);
        Color testColor = colors.get(colors.size() - 1);
        assertThat(testColor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testColor.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the Color in ElasticSearch
        Color colorEs = colorSearchRepository.findOne(testColor.getId());
        assertThat(colorEs).isEqualToComparingFieldByField(testColor);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = colorRepository.findAll().size();
        // set the field null
        color.setName(null);

        // Create the Color, which fails.

        restColorMockMvc.perform(post("/api/colors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(color)))
                .andExpect(status().isBadRequest());

        List<Color> colors = colorRepository.findAll();
        assertThat(colors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllColors() throws Exception {
        // Initialize the database
        colorRepository.saveAndFlush(color);

        // Get all the colors
        restColorMockMvc.perform(get("/api/colors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(color.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getColor() throws Exception {
        // Initialize the database
        colorRepository.saveAndFlush(color);

        // Get the color
        restColorMockMvc.perform(get("/api/colors/{id}", color.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(color.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingColor() throws Exception {
        // Get the color
        restColorMockMvc.perform(get("/api/colors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateColor() throws Exception {
        // Initialize the database
        colorRepository.saveAndFlush(color);
        colorSearchRepository.save(color);
        int databaseSizeBeforeUpdate = colorRepository.findAll().size();

        // Update the color
        Color updatedColor = new Color();
        updatedColor.setId(color.getId());
        updatedColor.setName(UPDATED_NAME);
        updatedColor.setCode(UPDATED_CODE);

        restColorMockMvc.perform(put("/api/colors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedColor)))
                .andExpect(status().isOk());

        // Validate the Color in the database
        List<Color> colors = colorRepository.findAll();
        assertThat(colors).hasSize(databaseSizeBeforeUpdate);
        Color testColor = colors.get(colors.size() - 1);
        assertThat(testColor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testColor.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the Color in ElasticSearch
        Color colorEs = colorSearchRepository.findOne(testColor.getId());
        assertThat(colorEs).isEqualToComparingFieldByField(testColor);
    }

    @Test
    @Transactional
    public void deleteColor() throws Exception {
        // Initialize the database
        colorRepository.saveAndFlush(color);
        colorSearchRepository.save(color);
        int databaseSizeBeforeDelete = colorRepository.findAll().size();

        // Get the color
        restColorMockMvc.perform(delete("/api/colors/{id}", color.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean colorExistsInEs = colorSearchRepository.exists(color.getId());
        assertThat(colorExistsInEs).isFalse();

        // Validate the database is empty
        List<Color> colors = colorRepository.findAll();
        assertThat(colors).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchColor() throws Exception {
        // Initialize the database
        colorRepository.saveAndFlush(color);
        colorSearchRepository.save(color);

        // Search the color
        restColorMockMvc.perform(get("/api/_search/colors?query=id:" + color.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(color.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }
}
