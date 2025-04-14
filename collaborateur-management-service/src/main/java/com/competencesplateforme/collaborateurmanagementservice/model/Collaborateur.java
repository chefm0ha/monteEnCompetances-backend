package com.competencesplateforme.collaborateurmanagementservice.model;

import jakarta.persistence.*;

@Entity
@Table(name="collaborateurs")
@PrimaryKeyJoinColumn(name = "user_id")
public class Collaborateur extends User {

    @Column(nullable = false)
    private String poste;

    // Constructeur par défaut
    public Collaborateur() {
        super();
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }
}