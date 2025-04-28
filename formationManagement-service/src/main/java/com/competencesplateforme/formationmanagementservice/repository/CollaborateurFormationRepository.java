package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.CollaborateurFormation;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurFormationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CollaborateurFormationRepository extends JpaRepository<CollaborateurFormation, CollaborateurFormationId> {

    List<CollaborateurFormation> findByCollaborateurId(UUID collaborateurId);

    List<CollaborateurFormation> findByFormationId(Integer formationId);

    @Query("SELECT cf FROM CollaborateurFormation cf WHERE cf.progress >= :minProgress")
    List<CollaborateurFormation> findByProgressGreaterThanEqual(BigDecimal minProgress);

    @Query("SELECT cf FROM CollaborateurFormation cf WHERE cf.isCertificationGenerated = true")
    List<CollaborateurFormation> findAllWithCertification();

    @Query("SELECT cf FROM CollaborateurFormation cf WHERE cf.collaborateur.id = :collaborateurId AND cf.progress = 100.00 AND cf.isCertificationGenerated = false")
    List<CollaborateurFormation> findCompletedFormationsWithoutCertification(UUID collaborateurId);

    @Query("SELECT COUNT(cf) FROM CollaborateurFormation cf WHERE cf.formation.id = :formationId")
    Long countParticipantsByFormation(Integer formationId);

    @Query("SELECT AVG(cf.progress) FROM CollaborateurFormation cf WHERE cf.formation.id = :formationId")
    BigDecimal getAverageProgressByFormation(Integer formationId);
}