package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe pour représenter la clé composite de la table collaborateurs_formations
 */
@Embeddable
public class CollaborateurFormationId implements Serializable {

    @Column(name = "collaborateur_id")
    private UUID collaborateurId;

    @Column(name = "formation_id")
    private Integer formationId;

    // Constructeurs
    public CollaborateurFormationId() {
    }

    public CollaborateurFormationId(UUID collaborateurId, Integer formationId) {
        this.collaborateurId = collaborateurId;
        this.formationId = formationId;
    }

    // Getters et Setters
    public UUID getCollaborateurId() {
        return collaborateurId;
    }

    public void setCollaborateurId(UUID collaborateurId) {
        this.collaborateurId = collaborateurId;
    }

    public Integer getFormationId() {
        return formationId;
    }

    public void setFormationId(Integer formationId) {
        this.formationId = formationId;
    }

    // Equals et HashCode pour la clé composite
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollaborateurFormationId that = (CollaborateurFormationId) o;
        return Objects.equals(collaborateurId, that.collaborateurId) &&
                Objects.equals(formationId, that.formationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collaborateurId, formationId);
    }
}