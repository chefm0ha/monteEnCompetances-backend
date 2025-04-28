package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.ChoixDTO;
import com.competencesplateforme.formationmanagementservice.model.Choix;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChoixMapper {

    public ChoixDTO toDTO(Choix choix) {
        if (choix == null) {
            return null;
        }

        ChoixDTO dto = new ChoixDTO();
        dto.setId(choix.getId());
        if (choix.getQuestion() != null) {
            dto.setQuestionId(choix.getQuestion().getId());
        }
        dto.setContenu(choix.getContenu());
        dto.setEstCorrect(choix.getEstCorrect());

        return dto;
    }

    public Choix toEntity(ChoixDTO dto) {
        if (dto == null) {
            return null;
        }

        Choix choix = new Choix();
        choix.setId(dto.getId());
        choix.setContenu(dto.getContenu());
        choix.setEstCorrect(dto.getEstCorrect());
        // La question est généralement définie séparément via le service

        return choix;
    }

    public void updateEntityFromDTO(ChoixDTO dto, Choix choix) {
        if (dto == null || choix == null) {
            return;
        }

        if (dto.getContenu() != null) {
            choix.setContenu(dto.getContenu());
        }
        if (dto.getEstCorrect() != null) {
            choix.setEstCorrect(dto.getEstCorrect());
        }
    }

    public List<ChoixDTO> toDTOList(List<Choix> choixList) {
        return choixList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}