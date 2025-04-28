package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.SupportDTO;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import com.competencesplateforme.formationmanagementservice.model.Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupportMapper {

    private final FileStorageService fileStorageService;

    @Autowired
    public SupportMapper(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public SupportDTO toDTO(Support support) {
        if (support == null) {
            return null;
        }

        SupportDTO dto = new SupportDTO();
        dto.setId(support.getId());
        if (support.getModule() != null) {
            dto.setModuleId(support.getModule().getId());
        }
        dto.setType(support.getType());
        dto.setLien(support.getLien());

        // Générer l'URL de téléchargement si un lien est disponible
        if (support.getLien() != null) {
            dto.setDownloadUrl(fileStorageService.getFileUrl(support.getLien()));
        }

        return dto;
    }

    public Support toEntity(SupportDTO dto) {
        if (dto == null) {
            return null;
        }

        Support support = new Support();
        support.setId(dto.getId());
        support.setType(dto.getType());
        support.setLien(dto.getLien());
        // Le module est généralement défini séparément via le service

        return support;
    }

    public void updateEntityFromDTO(SupportDTO dto, Support support) {
        if (dto == null || support == null) {
            return;
        }

        if (dto.getType() != null) {
            support.setType(dto.getType());
        }
        if (dto.getLien() != null) {
            support.setLien(dto.getLien());
        }
    }

    public List<SupportDTO> toDTOList(List<Support> supports) {
        return supports.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}