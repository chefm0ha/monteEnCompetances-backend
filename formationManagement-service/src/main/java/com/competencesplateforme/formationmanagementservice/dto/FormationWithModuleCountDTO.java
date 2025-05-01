package com.competencesplateforme.formationmanagementservice.dto;

public class FormationWithModuleCountDTO extends FormationDTO {
    private Integer numberOfModules;

    public Integer getNumberOfModules() {
        return numberOfModules;
    }

    public void setNumberOfModules(Integer numberOfModules) {
        this.numberOfModules = numberOfModules;
    }
}
