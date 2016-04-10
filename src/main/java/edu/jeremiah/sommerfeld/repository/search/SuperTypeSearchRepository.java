package edu.jeremiah.sommerfeld.repository.search;

import edu.jeremiah.sommerfeld.domain.SuperType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the SuperType entity.
 */
public interface SuperTypeSearchRepository extends ElasticsearchRepository<SuperType, Long> {
}
