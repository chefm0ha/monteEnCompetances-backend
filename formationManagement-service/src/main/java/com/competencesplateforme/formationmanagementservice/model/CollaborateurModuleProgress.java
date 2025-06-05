package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// CollaborateurModuleProgress.java
@Entity
@Table(name = "collaborateur_module_progress")
public class CollaborateurModuleProgress {

    @EmbeddedId
    private CollaborateurModuleProgressId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collaborateurId")
    @JoinColumn(name = "collaborateur_id")
    private Collaborateur collaborateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("moduleId")
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "quiz_score", precision = 5, scale = 2)
    private BigDecimal quizScore;

    // Constructors
    public CollaborateurModuleProgress() {}

    public CollaborateurModuleProgress(Collaborateur collaborateur, Module module) {
        this.collaborateur = collaborateur;
        this.module = module;
        this.id = new CollaborateurModuleProgressId(collaborateur.getId(), module.getId());
        this.isCompleted = false;
    }

    // Method to mark as completed
    public void markAsCompleted(BigDecimal quizScore) {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        this.quizScore = quizScore;
    }

    // Getters and Setters
    public CollaborateurModuleProgressId getId() { return id; }
    public void setId(CollaborateurModuleProgressId id) { this.id = id; }

    public Collaborateur getCollaborateur() { return collaborateur; }
    public void setCollaborateur(Collaborateur collaborateur) { this.collaborateur = collaborateur; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public BigDecimal getQuizScore() { return quizScore; }
    public void setQuizScore(BigDecimal quizScore) { this.quizScore = quizScore; }
}
