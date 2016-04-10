package edu.jeremiah.sommerfeld.web.rest;

import edu.jeremiah.sommerfeld.MagicLinkApp;
import edu.jeremiah.sommerfeld.domain.SubType;
import edu.jeremiah.sommerfeld.repository.SubTypeRepository;
import edu.jeremiah.sommerfeld.repository.search.SubTypeSearchRepository;

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
 * Test class for the SubTypeResource REST controller.
 *
 * @see SubTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MagicLinkApp.class)
@WebAppConfiguration
@IntegrationTest
public class SubTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private SubTypeRepository subTypeRepository;

    @Inject
    private SubTypeSearchRepository subTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSubTypeMockMvc;

    private SubType subType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SubTypeResource subTypeResource = new SubTypeResource();
        ReflectionTestUtils.setField(subTypeResource, "subTypeSearchRepository", subTypeSearchRepository);
        ReflectionTestUtils.setField(subTypeResource, "subTypeRepository", subTypeRepository);
        this.restSubTypeMockMvc = MockMvcBuilders.standaloneSetup(subTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        subTypeSearchRepository.deleteAll();
        subType = new SubType();
        subType.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createSubType() throws Exception {
        int databaseSizeBeforeCreate = subTypeRepository.findAll().size();

        // Create the SubType

        restSubTypeMockMvc.perform(post("/api/sub-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subType)))
                .andExpect(status().isCreated());

        // Validate the SubType in the database
        List<SubType> subTypes = subTypeRepository.findAll();
        assertThat(subTypes).hasSize(databaseSizeBeforeCreate + 1);
        SubType testSubType = subTypes.get(subTypes.size() - 1);
        assertThat(testSubType.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the SubType in ElasticSearch
        SubType subTypeEs = subTypeSearchRepository.findOne(testSubType.getId());
        assertThat(subTypeEs).isEqualToComparingFieldByField(testSubType);
    }

    @Test
    @Transactional
    public void getAllSubTypes() throws Exception {
        // Initialize the database
        subTypeRepository.saveAndFlush(subType);

        // Get all the subTypes
        restSubTypeMockMvc.perform(get("/api/sub-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(subType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getSubType() throws Exception {
        // Initialize the database
        subTypeRepository.saveAndFlush(subType);

        // Get the subType
        restSubTypeMockMvc.perform(get("/api/sub-types/{id}", subType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(subType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSubType() throws Exception {
        // Get the subType
        restSubTypeMockMvc.perform(get("/api/sub-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSubType() throws Exception {
        // Initialize the database
        subTypeRepository.saveAndFlush(subType);
        subTypeSearchRepository.save(subType);
        int databaseSizeBeforeUpdate = subTypeRepository.findAll().size();

        // Update the subType
        SubType updatedSubType = new SubType();
        updatedSubType.setId(subType.getId());
        updatedSubType.setName(UPDATED_NAME);

        restSubTypeMockMvc.perform(put("/api/sub-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSubType)))
                .andExpect(status().isOk());

        // Validate the SubType in the database
        List<SubType> subTypes = subTypeRepository.findAll();
        assertThat(subTypes).hasSize(databaseSizeBeforeUpdate);
        SubType testSubType = subTypes.get(subTypes.size() - 1);
        assertThat(testSubType.getName()).isEqualTo(UPDATED_NAME);

        // Validate the SubType in ElasticSearch
        SubType subTypeEs = subTypeSearchRepository.findOne(testSubType.getId());
        assertThat(subTypeEs).isEqualToComparingFieldByField(testSubType);
    }

    @Test
    @Transactional
    public void deleteSubType() throws Exception {
        // Initialize the database
        subTypeRepository.saveAndFlush(subType);
        subTypeSearchRepository.save(subType);
        int databaseSizeBeforeDelete = subTypeRepository.findAll().size();

        // Get the subType
        restSubTypeMockMvc.perform(delete("/api/sub-types/{id}", subType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean subTypeExistsInEs = subTypeSearchRepository.exists(subType.getId());
        assertThat(subTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<SubType> subTypes = subTypeRepository.findAll();
        assertThat(subTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSubType() throws Exception {
        // Initialize the database
        subTypeRepository.saveAndFlush(subType);
        subTypeSearchRepository.save(subType);

        // Search the subType
        restSubTypeMockMvc.perform(get("/api/_search/sub-types?query=id:" + subType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
