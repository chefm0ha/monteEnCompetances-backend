package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.ModuleDTO;
import com.competencesplateforme.formationmanagementservice.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModuleMapper {

    private final SupportMapper supportMapper;
    private final QuizMapper quizMapper;

    @Autowired
    public ModuleMapper(SupportMapper supportMapper, QuizMapper quizMapper) {
        this.supportMapper = supportMapper;
        this.quizMapper = quizMapper;
    }

    public ModuleDTO toDTO(Module module) {
        if (module == null) {
            return null;
        }

        ModuleDTO dto = new ModuleDTO();
        dto.setId(module.getId());
        if (module.getFormation() != null) {
            dto.setFormationId(module.getFormation().getId());
        }
        dto.setTitre(module.getTitre());
        dto.setDescription(module.getDescription());

        return dto;
    }

    public ModuleDTO toDTOWithRelations(Module module) {
        ModuleDTO dto = toDTO(module);
        if (dto != null) {
            if (module.getSupports() != null) {
                dto.setSupports(module.getSupports().stream()
                        .map(supportMapper::toDTO)
                        .collect(Collectors.toSet()));
            }
            if (module.getQuizs() != null) {
                dto.setQuizs(module.getQuizs().stream()
                        .map(quizMapper::toDTO)
                        .collect(Collectors.toSet()));
            }
        }
        return dto;
    }

    public Module toEntity(ModuleDTO dto) {
        if (dto == null) {
            return null;
        }

        Module module = new Module();
        module.setId(dto.getId());
        module.setTitre(dto.getTitre());
        module.setDescription(dto.getDescription());
        // La formation est généralement définie séparément via le service

        return module;
    }

    public void updateEntityFromDTO(ModuleDTO dto, Module module) {
        if (dto == null || module == null) {
            return;
        }

        if (dto.getTitre() != null) {
            module.setTitre(dto.getTitre());
        }
        if (dto.getDescription() != null) {
            module.setDescription(dto.getDescription());
        }
    }

    public List<ModuleDTO> toDTOList(List<Module> modules) {
        return modules.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}