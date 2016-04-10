package edu.jeremiah.sommerfeld.repository;

import edu.jeremiah.sommerfeld.domain.Card;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Card entity.
 */
public interface CardRepository extends JpaRepository<Card,Long> {

    @Query("select distinct card from Card card left join fetch card.types left join fetch card.subTypes left join fetch card.colors")
    List<Card> findAllWithEagerRelationships();

    @Query("select card from Card card left join fetch card.types left join fetch card.subTypes left join fetch card.colors where card.id =:id")
    Card findOneWithEagerRelationships(@Param("id") Long id);

}
