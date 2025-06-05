package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

// CollaborateurSupportProgressId.java
@Embeddable
public class CollaborateurSupportProgressId implements Serializable {

    @Column(name = "collaborateur_id")
    private UUID collaborateurId;

    @Column(name = "support_id")
    private Integer supportId;

    public CollaborateurSupportProgressId() {}

    public CollaborateurSupportProgressId(UUID collaborateurId, Integer supportId) {
        this.collaborateurId = collaborateurId;
        this.supportId = supportId;
    }

    // Getters, setters, equals, hashCode
    public UUID getCollaborateurId() { return collaborateurId; }
    public void setCollaborateurId(UUID collaborateurId) { this.collaborateurId = collaborateurId; }

    public Integer getSupportId() { return supportId; }
    public void setSupportId(Integer supportId) { this.supportId = supportId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollaborateurSupportProgressId that = (CollaborateurSupportProgressId) o;
        return Objects.equals(collaborateurId, that.collaborateurId) &&
                Objects.equals(supportId, that.supportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collaborateurId, supportId);
    }
}
