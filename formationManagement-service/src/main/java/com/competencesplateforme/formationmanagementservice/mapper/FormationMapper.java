package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.FormationDTO;
import com.competencesplateforme.formationmanagementservice.dto.FormationSummaryDTO;
import com.competencesplateforme.formationmanagementservice.model.Formation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FormationMapper {

    private final ModuleMapper moduleMapper;

    @Autowired
    public FormationMapper(ModuleMapper moduleMapper) {
        this.moduleMapper = moduleMapper;
    }

    public FormationDTO toDTO(Formation formation) {
        if (formation == null) {
            return null;
        }

        FormationDTO dto = new FormationDTO();
        dto.setId(formation.getId());
        dto.setTitre(formation.getTitre());
        dto.setDescription(formation.getDescription());
        dto.setType(formation.getType());
        dto.setLienPhoto(formation.getLienPhoto());
        dto.setDuree(formation.getDuree());

        return dto;
    }

    public FormationDTO toDTOWithModules(Formation formation) {
        FormationDTO dto = toDTO(formation);
        if (dto != null && formation.getModules() != null) {
            dto.setModules(formation.getModules().stream()
                    .map(moduleMapper::toDTO)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public Formation toEntity(FormationDTO dto) {
        if (dto == null) {
            return null;
        }

        Formation formation = new Formation();
        formation.setId(dto.getId());
        formation.setTitre(dto.getTitre());
        formation.setDescription(dto.getDescription());
        formation.setType(dto.getType());
        formation.setLienPhoto(dto.getLienPhoto());
        formation.setDuree(dto.getDuree());

        return formation;
    }

    public List<FormationSummaryDTO> toSummaryDTOList(List<Formation> formations) {
        return formations.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public FormationSummaryDTO toSummaryDTO(Formation formation) {
        if (formation == null) {
            return null;
        }

        FormationSummaryDTO summaryDTO = new FormationSummaryDTO();
        summaryDTO.setId(formation.getId());
        summaryDTO.setTitre(formation.getTitre());
        summaryDTO.setType(formation.getType());
        summaryDTO.setLienPhoto(formation.getLienPhoto());
        summaryDTO.setDuree(formation.getDuree());

        // Calculate the module count directly from the formation entity
        if (formation.getModules() != null) {
            summaryDTO.setNombreModules((long) formation.getModules().size());
        } else {
            summaryDTO.setNombreModules(0L);
        }

        return summaryDTO;
    }

    public void updateEntityFromDTO(FormationDTO dto, Formation formation) {
        if (dto == null || formation == null) {
            return;
        }

        if (dto.getTitre() != null) {
            formation.setTitre(dto.getTitre());
        }
        if (dto.getDescription() != null) {
            formation.setDescription(dto.getDescription());
        }
        if (dto.getType() != null) {
            formation.setType(dto.getType());
        }
        if (dto.getLienPhoto() != null) {
            formation.setLienPhoto(dto.getLienPhoto());
        }
        if (dto.getDuree() != null) {
            formation.setDuree(dto.getDuree());
        }
    }

    public List<FormationDTO> toDTOList(List<Formation> formations) {
        return formations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}