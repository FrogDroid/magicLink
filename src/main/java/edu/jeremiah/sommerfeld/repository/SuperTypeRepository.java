package edu.jeremiah.sommerfeld.repository;

import edu.jeremiah.sommerfeld.domain.SuperType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SuperType entity.
 */
public interface SuperTypeRepository extends JpaRepository<SuperType,Long> {

}
