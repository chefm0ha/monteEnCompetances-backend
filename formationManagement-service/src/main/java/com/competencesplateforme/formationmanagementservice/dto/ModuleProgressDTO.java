package com.competencesplateforme.formationmanagementservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ModuleProgressDTO {
    private UUID collaborateurId;
    private Integer moduleId;
    private String moduleTitle;
    private Integer moduleOrder;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private BigDecimal quizScore;
    private Boolean isUnlocked;
    private Boolean allSupportsWatched;
    private Integer totalSupports;
    private Integer watchedSupports;

    // Constructors
    public ModuleProgressDTO() {}

    public ModuleProgressDTO(UUID collaborateurId, Integer moduleId, String moduleTitle,
                             Integer moduleOrder, Boolean isCompleted, Boolean isUnlocked) {
        this.collaborateurId = collaborateurId;
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.moduleOrder = moduleOrder;
        this.isCompleted = isCompleted;
        this.isUnlocked = isUnlocked;
    }

    // Getters and Setters
    public UUID getCollaborateurId() { return collaborateurId; }
    public void setCollaborateurId(UUID collaborateurId) { this.collaborateurId = collaborateurId; }

    public Integer getModuleId() { return moduleId; }
    public void setModuleId(Integer moduleId) { this.moduleId = moduleId; }

    public String getModuleTitle() { return moduleTitle; }
    public void setModuleTitle(String moduleTitle) { this.moduleTitle = moduleTitle; }

    public Integer getModuleOrder() { return moduleOrder; }
    public void setModuleOrder(Integer moduleOrder) { this.moduleOrder = moduleOrder; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public BigDecimal getQuizScore() { return quizScore; }
    public void setQuizScore(BigDecimal quizScore) { this.quizScore = quizScore; }

    public Boolean getIsUnlocked() { return isUnlocked; }
    public void setIsUnlocked(Boolean isUnlocked) { this.isUnlocked = isUnlocked; }

    public Boolean getAllSupportsWatched() { return allSupportsWatched; }
    public void setAllSupportsWatched(Boolean allSupportsWatched) { this.allSupportsWatched = allSupportsWatched; }

    public Integer getTotalSupports() { return totalSupports; }
    public void setTotalSupports(Integer totalSupports) { this.totalSupports = totalSupports; }

    public Integer getWatchedSupports() { return watchedSupports; }
    public void setWatchedSupports(Integer watchedSupports) { this.watchedSupports = watchedSupports; }

    // Helper methods
    public String getStatus() {
        if (isCompleted != null && isCompleted) {
            return "COMPLETED";
        } else if (isUnlocked != null && isUnlocked) {
            return "UNLOCKED";
        } else {
            return "LOCKED";
        }
    }

    public Double getSupportWatchedPercentage() {
        if (totalSupports == null || totalSupports == 0) {
            return 0.0;
        }
        return (watchedSupports != null ? watchedSupports : 0) * 100.0 / totalSupports;
    }
}