package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Formation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT DISTINCT f FROM Formation f LEFT JOIN FETCH f.modules")
    List<Formation> findAllWithModules();

    @Query("SELECT f, COUNT(m) FROM Formation f LEFT JOIN f.modules m GROUP BY f.id")
    List<Object[]> findAllWithModuleCount();

    @Query("SELECT f.type, COUNT(f) FROM Formation f GROUP BY f.type ORDER BY COUNT(f) DESC")
    List<Object[]> getFormationCountByCategory();

    @Query("SELECT f FROM Formation f ORDER BY f.id DESC")
    List<Formation> findTop5ByOrderByIdDesc(Pageable pageable);

    // For convenience method
    default List<Formation> findTop5ByOrderByIdDesc() {
        return findTop5ByOrderByIdDesc((Pageable) PageRequest.of(0, 5));
    }
}