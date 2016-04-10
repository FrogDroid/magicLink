package edu.jeremiah.sommerfeld.repository.search;

import edu.jeremiah.sommerfeld.domain.SubType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the SubType entity.
 */
public interface SubTypeSearchRepository extends ElasticsearchRepository<SubType, Long> {
}
