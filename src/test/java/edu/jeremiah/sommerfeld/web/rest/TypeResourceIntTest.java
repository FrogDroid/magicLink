package edu.jeremiah.sommerfeld.web.rest;

import edu.jeremiah.sommerfeld.MagicLinkApp;
import edu.jeremiah.sommerfeld.domain.Type;
import edu.jeremiah.sommerfeld.repository.TypeRepository;
import edu.jeremiah.sommerfeld.repository.search.TypeSearchRepository;

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
 * Test class for the TypeResource REST controller.
 *
 * @see TypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MagicLinkApp.class)
@WebAppConfiguration
@IntegrationTest
public class TypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private TypeRepository typeRepository;

    @Inject
    private TypeSearchRepository typeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTypeMockMvc;

    private Type type;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeResource typeResource = new TypeResource();
        ReflectionTestUtils.setField(typeResource, "typeSearchRepository", typeSearchRepository);
        ReflectionTestUtils.setField(typeResource, "typeRepository", typeRepository);
        this.restTypeMockMvc = MockMvcBuilders.standaloneSetup(typeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        typeSearchRepository.deleteAll();
        type = new Type();
        type.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createType() throws Exception {
        int databaseSizeBeforeCreate = typeRepository.findAll().size();

        // Create the Type

        restTypeMockMvc.perform(post("/api/types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(type)))
                .andExpect(status().isCreated());

        // Validate the Type in the database
        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeCreate + 1);
        Type testType = types.get(types.size() - 1);
        assertThat(testType.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Type in ElasticSearch
        Type typeEs = typeSearchRepository.findOne(testType.getId());
        assertThat(typeEs).isEqualToComparingFieldByField(testType);
    }

    @Test
    @Transactional
    public void getAllTypes() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);

        // Get all the types
        restTypeMockMvc.perform(get("/api/types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(type.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getType() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);

        // Get the type
        restTypeMockMvc.perform(get("/api/types/{id}", type.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(type.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingType() throws Exception {
        // Get the type
        restTypeMockMvc.perform(get("/api/types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateType() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);
        typeSearchRepository.save(type);
        int databaseSizeBeforeUpdate = typeRepository.findAll().size();

        // Update the type
        Type updatedType = new Type();
        updatedType.setId(type.getId());
        updatedType.setName(UPDATED_NAME);

        restTypeMockMvc.perform(put("/api/types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedType)))
                .andExpect(status().isOk());

        // Validate the Type in the database
        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeUpdate);
        Type testType = types.get(types.size() - 1);
        assertThat(testType.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Type in ElasticSearch
        Type typeEs = typeSearchRepository.findOne(testType.getId());
        assertThat(typeEs).isEqualToComparingFieldByField(testType);
    }

    @Test
    @Transactional
    public void deleteType() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);
        typeSearchRepository.save(type);
        int databaseSizeBeforeDelete = typeRepository.findAll().size();

        // Get the type
        restTypeMockMvc.perform(delete("/api/types/{id}", type.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean typeExistsInEs = typeSearchRepository.exists(type.getId());
        assertThat(typeExistsInEs).isFalse();

        // Validate the database is empty
        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchType() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);
        typeSearchRepository.save(type);

        // Search the type
        restTypeMockMvc.perform(get("/api/_search/types?query=id:" + type.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(type.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
