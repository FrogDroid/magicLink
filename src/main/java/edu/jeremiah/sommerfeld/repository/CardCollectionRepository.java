package edu.jeremiah.sommerfeld.repository;

import edu.jeremiah.sommerfeld.domain.CardCollection;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the CardCollection entity.
 */
public interface CardCollectionRepository extends JpaRepository<CardCollection,Long> {

}
