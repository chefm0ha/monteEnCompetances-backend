package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.ModuleDTO;
import com.competencesplateforme.formationmanagementservice.dto.ModuleWithFormationDTO;
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
        dto.setOrdre(module.getOrdre());

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
        module.setOrdre(dto.getOrdre());
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
        if (dto.getOrdre() != null) {
            module.setOrdre(dto.getOrdre());
        }
    }

    public List<ModuleDTO> toDTOList(List<Module> modules) {
        return modules.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Nouvelle méthode pour mapper les modules avec formation
    public ModuleWithFormationDTO toModuleWithFormationDTO(Object[] result) {
        if (result == null || result.length < 3) {
            return null;
        }

        Module module = (Module) result[0];
        String formationTitre = (String) result[1];
        // HQL SIZE() function returns Integer, not Long
        Integer nombreSupports = (Integer) result[2];

        ModuleWithFormationDTO dto = new ModuleWithFormationDTO();
        dto.setId(module.getId());
        dto.setTitre(module.getTitre());
        dto.setDescription(module.getDescription());

        if (module.getFormation() != null) {
            dto.setFormationId(module.getFormation().getId());
            dto.setFormationTitre(formationTitre);
        }

        dto.setNombreSupports(nombreSupports != null ? nombreSupports : 0);

        return dto;
    }

    public List<ModuleWithFormationDTO> toModuleWithFormationDTOList(List<Object[]> results) {
        return results.stream()
                .map(this::toModuleWithFormationDTO)
                .collect(Collectors.toList());
    }
}