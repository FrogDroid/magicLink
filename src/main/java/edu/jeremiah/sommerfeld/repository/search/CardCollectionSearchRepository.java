package edu.jeremiah.sommerfeld.repository.search;

import edu.jeremiah.sommerfeld.domain.CardCollection;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the CardCollection entity.
 */
public interface CardCollectionSearchRepository extends ElasticsearchRepository<CardCollection, Long> {
}
