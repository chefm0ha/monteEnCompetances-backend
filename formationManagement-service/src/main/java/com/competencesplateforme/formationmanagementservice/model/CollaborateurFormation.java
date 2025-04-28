package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "collaborateurs_formations")
public class CollaborateurFormation {

    @EmbeddedId
    private CollaborateurFormationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collaborateurId")
    @JoinColumn(name = "collaborateur_id")
    private Collaborateur collaborateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("formationId")
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @Column(precision = 5, scale = 2)
    private BigDecimal progress;

    @Column(name = "is_certification_generated")
    private Boolean isCertificationGenerated;

    // Constructeurs
    public CollaborateurFormation() {
    }

    public CollaborateurFormation(Collaborateur collaborateur, Formation formation) {
        this.collaborateur = collaborateur;
        this.formation = formation;
        this.id = new CollaborateurFormationId(collaborateur.getId(), formation.getId());
        this.progress = BigDecimal.ZERO;
        this.isCertificationGenerated = false;
    }

    // Getters et Setters
    public CollaborateurFormationId getId() {
        return id;
    }

    public void setId(CollaborateurFormationId id) {
        this.id = id;
    }

    public Collaborateur getCollaborateur() {
        return collaborateur;
    }

    public void setCollaborateur(Collaborateur collaborateur) {
        this.collaborateur = collaborateur;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public Boolean getIsCertificationGenerated() {
        return isCertificationGenerated;
    }

    public void setIsCertificationGenerated(Boolean isCertificationGenerated) {
        this.isCertificationGenerated = isCertificationGenerated;
    }

    // Autres méthodes utiles

    /**
     * Met à jour la progression et génère un certificat si nécessaire
     * @param newProgress La nouvelle progression (0-100)
     */
    public void updateProgress(BigDecimal newProgress) {
        this.progress = newProgress;

        // Si la progression est à 100%, on peut générer un certificat
        if (this.progress.compareTo(new BigDecimal("100.00")) >= 0 && !this.isCertificationGenerated) {
            this.isCertificationGenerated = true;
            // Logique pour générer un certificat pourrait être ajoutée ici
        }
    }
}