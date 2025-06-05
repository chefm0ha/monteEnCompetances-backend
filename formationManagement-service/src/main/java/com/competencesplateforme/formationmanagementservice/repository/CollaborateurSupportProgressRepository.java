// CollaborateurSupportProgressRepository.java
package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.CollaborateurModuleProgress;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurModuleProgressId;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurSupportProgress;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurSupportProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollaborateurSupportProgressRepository extends JpaRepository<CollaborateurSupportProgress, CollaborateurSupportProgressId> {

    List<CollaborateurSupportProgress> findByCollaborateurId(UUID collaborateurId);

    List<CollaborateurSupportProgress> findBySupportId(Integer supportId);

    @Query("SELECT csp FROM CollaborateurSupportProgress csp WHERE csp.collaborateur.id = :collaborateurId AND csp.support.module.id = :moduleId")
    List<CollaborateurSupportProgress> findByCollaborateurIdAndModuleId(@Param("collaborateurId") UUID collaborateurId, @Param("moduleId") Integer moduleId);

    @Query("SELECT COUNT(csp) FROM CollaborateurSupportProgress csp WHERE csp.collaborateur.id = :collaborateurId AND csp.support.module.id = :moduleId")
    Long countSeenSupportsByCollaborateurAndModule(@Param("collaborateurId") UUID collaborateurId, @Param("moduleId") Integer moduleId);

    boolean existsByCollaborateurIdAndSupportId(UUID collaborateurId, Integer supportId);
}