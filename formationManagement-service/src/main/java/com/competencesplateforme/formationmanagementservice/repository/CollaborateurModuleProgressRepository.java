

// CollaborateurModuleProgressRepository.java
package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.CollaborateurModuleProgress;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurModuleProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollaborateurModuleProgressRepository extends JpaRepository<CollaborateurModuleProgress, CollaborateurModuleProgressId> {

    List<CollaborateurModuleProgress> findByCollaborateurId(UUID collaborateurId);

    List<CollaborateurModuleProgress> findByModuleId(Integer moduleId);

    @Query("SELECT cmp FROM CollaborateurModuleProgress cmp WHERE cmp.collaborateur.id = :collaborateurId AND cmp.module.formation.id = :formationId ORDER BY cmp.module.ordre")
    List<CollaborateurModuleProgress> findByCollaborateurIdAndFormationIdOrderByModuleOrder(@Param("collaborateurId") UUID collaborateurId, @Param("formationId") Integer formationId);

    @Query("SELECT cmp FROM CollaborateurModuleProgress cmp WHERE cmp.collaborateur.id = :collaborateurId AND cmp.isCompleted = true")
    List<CollaborateurModuleProgress> findCompletedModulesByCollaborateur(@Param("collaborateurId") UUID collaborateurId);

    @Query("SELECT cmp FROM CollaborateurModuleProgress cmp WHERE cmp.collaborateur.id = :collaborateurId AND cmp.module.formation.id = :formationId AND cmp.isCompleted = true ORDER BY cmp.module.ordre")
    List<CollaborateurModuleProgress> findCompletedModulesByCollaborateurAndFormation(@Param("collaborateurId") UUID collaborateurId, @Param("formationId") Integer formationId);

    Optional<CollaborateurModuleProgress> findByCollaborateurIdAndModuleId(UUID collaborateurId, Integer moduleId);

    boolean existsByCollaborateurIdAndModuleIdAndIsCompletedTrue(UUID collaborateurId, Integer moduleId);
}