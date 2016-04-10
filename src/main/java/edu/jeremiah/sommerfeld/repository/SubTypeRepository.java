package edu.jeremiah.sommerfeld.repository;

import edu.jeremiah.sommerfeld.domain.SubType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SubType entity.
 */
public interface SubTypeRepository extends JpaRepository<SubType,Long> {

}
