package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.CollaborateurFormationDTO;
import com.competencesplateforme.formationmanagementservice.model.CollaborateurFormation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollaborateurFormationMapper {

    public CollaborateurFormationDTO toDTO(CollaborateurFormation collaborateurFormation) {
        if (collaborateurFormation == null) {
            return null;
        }

        CollaborateurFormationDTO dto = new CollaborateurFormationDTO();
        dto.setCollaborateurId(collaborateurFormation.getCollaborateur().getId());
        dto.setFormationId(collaborateurFormation.getFormation().getId());
        dto.setProgress(collaborateurFormation.getProgress());
        dto.setIsCertificationGenerated(collaborateurFormation.getIsCertificationGenerated());

        // Ajouter les informations complémentaires si disponibles
        if (collaborateurFormation.getCollaborateur() != null) {
            dto.setCollaborateurNom(collaborateurFormation.getCollaborateur().getLastName());
            dto.setCollaborateurPrenom(collaborateurFormation.getCollaborateur().getFirstName());
        }

        if (collaborateurFormation.getFormation() != null) {
            dto.setFormationTitre(collaborateurFormation.getFormation().getTitre());
            dto.setFormationDescription(collaborateurFormation.getFormation().getDescription());
        }

        return dto;
    }

    public CollaborateurFormation toEntity(CollaborateurFormationDTO dto) {
        if (dto == null) {
            return null;
        }

        // Cette méthode n'est généralement pas utilisée directement
        // car les entités Collaborateur et Formation doivent être récupérées
        // depuis la base de données

        // L'instanciation complète se fait généralement dans le service

        return null;
    }

    public void updateEntityFromDTO(CollaborateurFormationDTO dto, CollaborateurFormation entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProgress() != null) {
            entity.setProgress(dto.getProgress());
        }
        if (dto.getIsCertificationGenerated() != null) {
            entity.setIsCertificationGenerated(dto.getIsCertificationGenerated());
        }
    }

    public List<CollaborateurFormationDTO> toDTOList(List<CollaborateurFormation> inscriptions) {
        return inscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}