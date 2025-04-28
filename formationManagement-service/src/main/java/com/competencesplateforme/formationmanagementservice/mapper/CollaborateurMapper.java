package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.CollaborateurDTO;
import com.competencesplateforme.formationmanagementservice.model.Collaborateur;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollaborateurMapper {

    /**
     * Convertit une entité Collaborateur en DTO
     * @param collaborateur L'entité à convertir
     * @return Le DTO correspondant
     */
    public CollaborateurDTO toDTO(Collaborateur collaborateur) {
        if (collaborateur == null) {
            return null;
        }

        CollaborateurDTO dto = new CollaborateurDTO();
        dto.setEmail(collaborateur.getEmail());
        // On ne copie pas le mot de passe pour des raisons de sécurité
        dto.setPassword(null);
        dto.setRole(collaborateur.getRole());
        dto.setFirstName(collaborateur.getFirstName());
        dto.setLastName(collaborateur.getLastName());
        dto.setPoste(collaborateur.getPoste());

        return dto;
    }

    /**
     * Convertit un DTO en entité Collaborateur
     * @param dto Le DTO à convertir
     * @return L'entité correspondante
     */
    public Collaborateur toEntity(CollaborateurDTO dto) {
        if (dto == null) {
            return null;
        }

        Collaborateur collaborateur = new Collaborateur();
        collaborateur.setEmail(dto.getEmail());
        collaborateur.setPassword(dto.getPassword());
        collaborateur.setRole(dto.getRole());
        collaborateur.setFirstName(dto.getFirstName());
        collaborateur.setLastName(dto.getLastName());
        collaborateur.setPoste(dto.getPoste());

        return collaborateur;
    }

    /**
     * Met à jour une entité existante avec les valeurs d'un DTO
     * @param collaborateur L'entité à mettre à jour
     * @param dto Le DTO contenant les nouvelles valeurs
     */
    public void updateEntityFromDTO(CollaborateurDTO dto, Collaborateur collaborateur) {
        if (dto == null || collaborateur == null) {
            return;
        }

        if (dto.getEmail() != null) {
            collaborateur.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            collaborateur.setPassword(dto.getPassword());
        }

        if (dto.getRole() != null) {
            collaborateur.setRole(dto.getRole());
        }

        if (dto.getFirstName() != null) {
            collaborateur.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            collaborateur.setLastName(dto.getLastName());
        }

        if (dto.getPoste() != null) {
            collaborateur.setPoste(dto.getPoste());
        }
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     * @param collaborateurs La liste d'entités
     * @return La liste de DTOs
     */
    public List<CollaborateurDTO> toDTOList(List<Collaborateur> collaborateurs) {
        return collaborateurs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
