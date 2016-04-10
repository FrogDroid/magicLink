package edu.jeremiah.sommerfeld.web.rest;

import edu.jeremiah.sommerfeld.MagicLinkApp;
import edu.jeremiah.sommerfeld.domain.SuperType;
import edu.jeremiah.sommerfeld.repository.SuperTypeRepository;
import edu.jeremiah.sommerfeld.repository.search.SuperTypeSearchRepository;

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
 * Test class for the SuperTypeResource REST controller.
 *
 * @see SuperTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MagicLinkApp.class)
@WebAppConfiguration
@IntegrationTest
public class SuperTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private SuperTypeRepository superTypeRepository;

    @Inject
    private SuperTypeSearchRepository superTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSuperTypeMockMvc;

    private SuperType superType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SuperTypeResource superTypeResource = new SuperTypeResource();
        ReflectionTestUtils.setField(superTypeResource, "superTypeSearchRepository", superTypeSearchRepository);
        ReflectionTestUtils.setField(superTypeResource, "superTypeRepository", superTypeRepository);
        this.restSuperTypeMockMvc = MockMvcBuilders.standaloneSetup(superTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        superTypeSearchRepository.deleteAll();
        superType = new SuperType();
        superType.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createSuperType() throws Exception {
        int databaseSizeBeforeCreate = superTypeRepository.findAll().size();

        // Create the SuperType

        restSuperTypeMockMvc.perform(post("/api/super-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(superType)))
                .andExpect(status().isCreated());

        // Validate the SuperType in the database
        List<SuperType> superTypes = superTypeRepository.findAll();
        assertThat(superTypes).hasSize(databaseSizeBeforeCreate + 1);
        SuperType testSuperType = superTypes.get(superTypes.size() - 1);
        assertThat(testSuperType.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the SuperType in ElasticSearch
        SuperType superTypeEs = superTypeSearchRepository.findOne(testSuperType.getId());
        assertThat(superTypeEs).isEqualToComparingFieldByField(testSuperType);
    }

    @Test
    @Transactional
    public void getAllSuperTypes() throws Exception {
        // Initialize the database
        superTypeRepository.saveAndFlush(superType);

        // Get all the superTypes
        restSuperTypeMockMvc.perform(get("/api/super-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(superType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getSuperType() throws Exception {
        // Initialize the database
        superTypeRepository.saveAndFlush(superType);

        // Get the superType
        restSuperTypeMockMvc.perform(get("/api/super-types/{id}", superType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(superType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSuperType() throws Exception {
        // Get the superType
        restSuperTypeMockMvc.perform(get("/api/super-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSuperType() throws Exception {
        // Initialize the database
        superTypeRepository.saveAndFlush(superType);
        superTypeSearchRepository.save(superType);
        int databaseSizeBeforeUpdate = superTypeRepository.findAll().size();

        // Update the superType
        SuperType updatedSuperType = new SuperType();
        updatedSuperType.setId(superType.getId());
        updatedSuperType.setName(UPDATED_NAME);

        restSuperTypeMockMvc.perform(put("/api/super-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSuperType)))
                .andExpect(status().isOk());

        // Validate the SuperType in the database
        List<SuperType> superTypes = superTypeRepository.findAll();
        assertThat(superTypes).hasSize(databaseSizeBeforeUpdate);
        SuperType testSuperType = superTypes.get(superTypes.size() - 1);
        assertThat(testSuperType.getName()).isEqualTo(UPDATED_NAME);

        // Validate the SuperType in ElasticSearch
        SuperType superTypeEs = superTypeSearchRepository.findOne(testSuperType.getId());
        assertThat(superTypeEs).isEqualToComparingFieldByField(testSuperType);
    }

    @Test
    @Transactional
    public void deleteSuperType() throws Exception {
        // Initialize the database
        superTypeRepository.saveAndFlush(superType);
        superTypeSearchRepository.save(superType);
        int databaseSizeBeforeDelete = superTypeRepository.findAll().size();

        // Get the superType
        restSuperTypeMockMvc.perform(delete("/api/super-types/{id}", superType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean superTypeExistsInEs = superTypeSearchRepository.exists(superType.getId());
        assertThat(superTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<SuperType> superTypes = superTypeRepository.findAll();
        assertThat(superTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSuperType() throws Exception {
        // Initialize the database
        superTypeRepository.saveAndFlush(superType);
        superTypeSearchRepository.save(superType);

        // Search the superType
        restSuperTypeMockMvc.perform(get("/api/_search/super-types?query=id:" + superType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(superType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
