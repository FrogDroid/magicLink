package edu.jeremiah.sommerfeld.repository.search;

import edu.jeremiah.sommerfeld.domain.Card;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Card entity.
 */
public interface CardSearchRepository extends ElasticsearchRepository<Card, Long> {
}
