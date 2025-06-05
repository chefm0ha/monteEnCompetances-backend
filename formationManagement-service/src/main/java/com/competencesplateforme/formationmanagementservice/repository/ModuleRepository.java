package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Find modules by formation and specific IDs
    @Query("SELECT m FROM Module m WHERE m.formation.id = :formationId AND m.id IN :moduleIds")
    List<Module> findByFormationIdAndIdIn(@Param("formationId") Integer formationId, @Param("moduleIds") List<Integer> moduleIds);

    // Update module order
    @Modifying
    @Query("UPDATE Module m SET m.ordre = :ordre WHERE m.id = :moduleId")
    void updateModuleOrder(@Param("moduleId") Integer moduleId, @Param("ordre") Integer ordre);

    // Get next order value for a formation
    @Query("SELECT COALESCE(MAX(m.ordre), 0) + 1 FROM Module m WHERE m.formation.id = :formationId")
    Integer getNextOrderValue(@Param("formationId") Integer formationId);

    @Query("SELECT m FROM Module m WHERE m.formation.id = :formationId ORDER BY m.ordre ASC")
    List<Module> findByFormationIdOrderByModuleOrder(@Param("formationId") Integer formationId);
}