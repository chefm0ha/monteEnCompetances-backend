package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    List<Module> findByFormationId(Integer formationId);

    List<Module> findByTitreContainingIgnoreCase(String titre);

    @Query("SELECT m, f.titre, " +
            "COALESCE(SIZE(m.supports), 0) as nombreSupports " +
            "FROM Module m " +
            "LEFT JOIN m.formation f " +
            "ORDER BY f.titre ASC, m.titre ASC")
    List<Object[]> findAllModulesWithFormationAndCounts();
}