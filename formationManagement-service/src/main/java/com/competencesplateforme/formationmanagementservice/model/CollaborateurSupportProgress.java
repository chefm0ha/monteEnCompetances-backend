// CollaborateurSupportProgress.java
package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "collaborateur_support_progress")
public class CollaborateurSupportProgress {

    @EmbeddedId
    private CollaborateurSupportProgressId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collaborateurId")
    @JoinColumn(name = "collaborateur_id")
    private Collaborateur collaborateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("supportId")
    @JoinColumn(name = "support_id")
    private Support support;

    @Column(name = "seen_at")
    private LocalDateTime seenAt;

    // Constructors
    public CollaborateurSupportProgress() {
    }

    public CollaborateurSupportProgress(Collaborateur collaborateur, Support support) {
        this.collaborateur = collaborateur;
        this.support = support;
        this.id = new CollaborateurSupportProgressId(collaborateur.getId(), support.getId());
        this.seenAt = LocalDateTime.now();
    }

    // Getters and Setters
    public CollaborateurSupportProgressId getId() { return id; }
    public void setId(CollaborateurSupportProgressId id) { this.id = id; }

    public Collaborateur getCollaborateur() { return collaborateur; }
    public void setCollaborateur(Collaborateur collaborateur) { this.collaborateur = collaborateur; }

    public Support getSupport() { return support; }
    public void setSupport(Support support) { this.support = support; }

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) { this.seenAt = seenAt; }
}