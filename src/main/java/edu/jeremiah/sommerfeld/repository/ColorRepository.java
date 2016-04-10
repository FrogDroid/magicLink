package edu.jeremiah.sommerfeld.repository;

import edu.jeremiah.sommerfeld.domain.Color;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Color entity.
 */
public interface ColorRepository extends JpaRepository<Color,Long> {

}
