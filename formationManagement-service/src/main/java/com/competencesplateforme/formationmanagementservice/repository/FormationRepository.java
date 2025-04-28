package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Integer> {

    List<Formation> findByType(String type);

    List<Formation> findByTitreContainingIgnoreCase(String titre);

    @Query("SELECT f FROM Formation f WHERE LOWER(f.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Formation> searchFormations(String keyword);
}