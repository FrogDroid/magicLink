package edu.jeremiah.sommerfeld.repository;

import edu.jeremiah.sommerfeld.domain.CardCollection;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the CardCollection entity.
 */
public interface CardCollectionRepository extends JpaRepository<CardCollection,Long> {

    @Query("select distinct cardCollection from CardCollection cardCollection left join fetch cardCollection.cards")
    List<CardCollection> findAllWithEagerRelationships();

    @Query("select cardCollection from CardCollection cardCollection left join fetch cardCollection.cards where cardCollection.id =:id")
    CardCollection findOneWithEagerRelationships(@Param("id") Long id);

}
