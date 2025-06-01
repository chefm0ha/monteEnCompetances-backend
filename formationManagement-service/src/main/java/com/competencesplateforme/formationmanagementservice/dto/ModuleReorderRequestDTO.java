package com.competencesplateforme.formationmanagementservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ModuleReorderRequestDTO {

    @NotNull(message = "La liste des IDs de modules ne peut pas être nulle")
    @NotEmpty(message = "La liste des IDs de modules ne peut pas être vide")
    private List<Integer> moduleIds;

    // Constructeurs
    public ModuleReorderRequestDTO() {
    }

    public ModuleReorderRequestDTO(List<Integer> moduleIds) {
        this.moduleIds = moduleIds;
    }

    // Getters et Setters
    public List<Integer> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Integer> moduleIds) {
        this.moduleIds = moduleIds;
    }

    @Override
    public String toString() {
        return "ModuleReorderRequestDTO{" +
                "moduleIds=" + moduleIds +
                '}';
    }
}