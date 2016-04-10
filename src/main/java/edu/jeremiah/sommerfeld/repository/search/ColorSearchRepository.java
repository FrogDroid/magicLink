package edu.jeremiah.sommerfeld.repository.search;

import edu.jeremiah.sommerfeld.domain.Color;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Color entity.
 */
public interface ColorSearchRepository extends ElasticsearchRepository<Color, Long> {
}
