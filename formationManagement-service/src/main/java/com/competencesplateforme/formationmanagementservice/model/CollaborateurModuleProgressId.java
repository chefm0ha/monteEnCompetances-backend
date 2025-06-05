package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

// CollaborateurModuleProgressId.java
@Embeddable
public class CollaborateurModuleProgressId implements Serializable {

    @Column(name = "collaborateur_id")
    private UUID collaborateurId;

    @Column(name = "module_id")
    private Integer moduleId;

    public CollaborateurModuleProgressId() {}

    public CollaborateurModuleProgressId(UUID collaborateurId, Integer moduleId) {
        this.collaborateurId = collaborateurId;
        this.moduleId = moduleId;
    }

    // Getters, setters, equals, hashCode
    public UUID getCollaborateurId() { return collaborateurId; }
    public void setCollaborateurId(UUID collaborateurId) { this.collaborateurId = collaborateurId; }

    public Integer getModuleId() { return moduleId; }
    public void setModuleId(Integer moduleId) { this.moduleId = moduleId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollaborateurModuleProgressId that = (CollaborateurModuleProgressId) o;
        return Objects.equals(collaborateurId, that.collaborateurId) &&
                Objects.equals(moduleId, that.moduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collaborateurId, moduleId);
    }
}
