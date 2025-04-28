package com.competencesplateforme.formationmanagementservice.model;

import com.competencesplateforme.formationmanagementservice.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

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