package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.model.Collaborateur;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurFormation;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurFormationId;
import com.competencesplateforme.formationmanagementservice.model.Formation;
import com.competencesplateforme.formationmanagementservice.repository.CollaborateurFormationRepository;
import com.competencesplateforme.formationmanagementservice.repository.CollaborateurRepository;
import com.competencesplateforme.formationmanagementservice.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollaborateurFormationService {

    private final CollaborateurFormationRepository collaborateurFormationRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final FormationRepository formationRepository;

    @Autowired
    public CollaborateurFormationService(CollaborateurFormationRepository collaborateurFormationRepository,
                                         CollaborateurRepository collaborateurRepository,
                                         FormationRepository formationRepository) {
        this.collaborateurFormationRepository = collaborateurFormationRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.formationRepository = formationRepository;
    }

    /**
     * Inscrit un collaborateur à une formation
     */
    @Transactional
    public Optional<CollaborateurFormation> inscrireCollaborateurAFormation(UUID collaborateurId, Integer formationId) {
        Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findById(collaborateurId);
        Optional<Formation> formationOpt = formationRepository.findById(formationId);

        if (collaborateurOpt.isPresent() && formationOpt.isPresent()) {
            Collaborateur collaborateur = collaborateurOpt.get();
            Formation formation = formationOpt.get();

            // Vérifier si l'inscription existe déjà
            CollaborateurFormationId id = new CollaborateurFormationId(collaborateurId, formationId);
            if (collaborateurFormationRepository.existsById(id)) {
                return Optional.empty(); // L'inscription existe déjà
            }

            // Créer une nouvelle inscription
            CollaborateurFormation inscription = new CollaborateurFormation(collaborateur, formation);
            inscription.setProgress(BigDecimal.ZERO);
            inscription.setIsCertificationGenerated(false);

            return Optional.of(collaborateurFormationRepository.save(inscription));
        }

        return Optional.empty();
    }

    /**
     * Récupère toutes les inscriptions d'un collaborateur
     */
    @Transactional(readOnly = true)
    public List<CollaborateurFormation> getFormationsForCollaborateur(UUID collaborateurId) {
        return collaborateurFormationRepository.findByCollaborateurId(collaborateurId);
    }

    /**
     * Récupère tous les collaborateurs inscrits à une formation
     */
    @Transactional(readOnly = true)
    public List<CollaborateurFormation> getCollaborateursForFormation(Integer formationId) {
        return collaborateurFormationRepository.findByFormationId(formationId);
    }

    /**
     * Met à jour la progression d'un collaborateur dans une formation
     */
    @Transactional
    public Optional<CollaborateurFormation> updateProgress(UUID collaborateurId, Integer formationId, BigDecimal newProgress) {
        CollaborateurFormationId id = new CollaborateurFormationId(collaborateurId, formationId);

        return collaborateurFormationRepository.findById(id)
                .map(inscription -> {
                    inscription.updateProgress(newProgress);
                    return collaborateurFormationRepository.save(inscription);
                });
    }

    /**
     * Génère un certificat pour une formation complétée
     */
    @Transactional
    public Optional<CollaborateurFormation> generateCertificate(UUID collaborateurId, Integer formationId) {
        CollaborateurFormationId id = new CollaborateurFormationId(collaborateurId, formationId);

        return collaborateurFormationRepository.findById(id)
                .map(inscription -> {
                    // Vérifier si la formation est complétée
                    if (inscription.getProgress().compareTo(new BigDecimal("100.00")) >= 0) {
                        inscription.setIsCertificationGenerated(true);
                        return collaborateurFormationRepository.save(inscription);
                    }
                    return inscription; // Pas de changement si la formation n'est pas complétée
                });
    }

    /**
     * Désinscrit un collaborateur d'une formation
     */
    @Transactional
    public boolean desinscrireCollaborateurDeFormation(UUID collaborateurId, Integer formationId) {
        CollaborateurFormationId id = new CollaborateurFormationId(collaborateurId, formationId);

        if (collaborateurFormationRepository.existsById(id)) {
            collaborateurFormationRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Trouve toutes les formations complétées sans certificat
     */
    @Transactional(readOnly = true)
    public List<CollaborateurFormation> getCompletedFormationsWithoutCertification(UUID collaborateurId) {
        return collaborateurFormationRepository.findCompletedFormationsWithoutCertification(collaborateurId);
    }

    /**
     * Compte le nombre de participants à une formation
     */
    @Transactional(readOnly = true)
    public Long countParticipantsByFormation(Integer formationId) {
        return collaborateurFormationRepository.countParticipantsByFormation(formationId);
    }

    /**
     * Calcule la progression moyenne pour une formation
     */
    @Transactional(readOnly = true)
    public BigDecimal getAverageProgressByFormation(Integer formationId) {
        return collaborateurFormationRepository.getAverageProgressByFormation(formationId);
    }

    /**
     * Récupère toutes les inscriptions avec une progression minimale
     */
    @Transactional(readOnly = true)
    public List<CollaborateurFormation> getInscriptionsByMinProgress(BigDecimal minProgress) {
        return collaborateurFormationRepository.findByProgressGreaterThanEqual(minProgress);
    }

    /**
     * Récupère toutes les inscriptions avec certificat généré
     */
    @Transactional(readOnly = true)
    public List<CollaborateurFormation> getAllWithCertification() {
        return collaborateurFormationRepository.findAllWithCertification();
    }

}