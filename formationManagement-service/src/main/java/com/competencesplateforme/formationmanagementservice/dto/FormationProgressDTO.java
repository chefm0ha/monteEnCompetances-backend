// FormationProgressDTO.java
package com.competencesplateforme.formationmanagementservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class FormationProgressDTO {
    private UUID collaborateurId;
    private Integer formationId;
    private String formationTitle;
    private BigDecimal overallProgress;
    private List<ModuleProgressDTO> modules;
    private ModuleProgressDTO nextUnlockedModule;
    private Integer totalModules;
    private Integer completedModules;
    private Boolean isFormationCompleted;

    // Constructors
    public FormationProgressDTO() {}

    public FormationProgressDTO(UUID collaborateurId, Integer formationId, String formationTitle) {
        this.collaborateurId = collaborateurId;
        this.formationId = formationId;
        this.formationTitle = formationTitle;
    }

    // Getters and Setters
    public UUID getCollaborateurId() { return collaborateurId; }
    public void setCollaborateurId(UUID collaborateurId) { this.collaborateurId = collaborateurId; }

    public Integer getFormationId() { return formationId; }
    public void setFormationId(Integer formationId) { this.formationId = formationId; }

    public String getFormationTitle() { return formationTitle; }
    public void setFormationTitle(String formationTitle) { this.formationTitle = formationTitle; }

    public BigDecimal getOverallProgress() { return overallProgress; }
    public void setOverallProgress(BigDecimal overallProgress) { this.overallProgress = overallProgress; }

    public List<ModuleProgressDTO> getModules() { return modules; }
    public void setModules(List<ModuleProgressDTO> modules) { this.modules = modules; }

    public ModuleProgressDTO getNextUnlockedModule() { return nextUnlockedModule; }
    public void setNextUnlockedModule(ModuleProgressDTO nextUnlockedModule) { this.nextUnlockedModule = nextUnlockedModule; }

    public Integer getTotalModules() { return totalModules; }
    public void setTotalModules(Integer totalModules) { this.totalModules = totalModules; }

    public Integer getCompletedModules() { return completedModules; }
    public void setCompletedModules(Integer completedModules) { this.completedModules = completedModules; }

    public Boolean getIsFormationCompleted() { return isFormationCompleted; }
    public void setIsFormationCompleted(Boolean isFormationCompleted) { this.isFormationCompleted = isFormationCompleted; }

    // Helper methods
    public String getFormattedProgress() {
        if (overallProgress != null) {
            return String.format("%.1f%%", overallProgress);
        }
        return "0%";
    }
}