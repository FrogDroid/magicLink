package edu.jeremiah.sommerfeld.repository.search;

import edu.jeremiah.sommerfeld.domain.Type;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Type entity.
 */
public interface TypeSearchRepository extends ElasticsearchRepository<Type, Long> {
}
