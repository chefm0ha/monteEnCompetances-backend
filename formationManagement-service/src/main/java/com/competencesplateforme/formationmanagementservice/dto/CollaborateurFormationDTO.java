package com.competencesplateforme.formationmanagementservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CollaborateurFormationDTO {

    private UUID collaborateurId;
    private Integer formationId;
    private BigDecimal progress;
    private Boolean isCertificationGenerated;

    // Informations complémentaires pour l'affichage
    private String collaborateurNom;
    private String collaborateurPrenom;
    private String formationTitre;
    private String formationDescription;
    private String formationLienPhoto;
    private String formationType;
    private Double formationDuree;

    // Constructeurs
    public CollaborateurFormationDTO() {
    }

    public CollaborateurFormationDTO(UUID collaborateurId, Integer formationId, BigDecimal progress, Boolean isCertificationGenerated) {
        this.collaborateurId = collaborateurId;
        this.formationId = formationId;
        this.progress = progress;
        this.isCertificationGenerated = isCertificationGenerated;
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

    public String getCollaborateurNom() {
        return collaborateurNom;
    }

    public void setCollaborateurNom(String collaborateurNom) {
        this.collaborateurNom = collaborateurNom;
    }

    public String getCollaborateurPrenom() {
        return collaborateurPrenom;
    }

    public void setCollaborateurPrenom(String collaborateurPrenom) {
        this.collaborateurPrenom = collaborateurPrenom;
    }

    public String getFormationTitre() {
        return formationTitre;
    }

    public void setFormationTitre(String formationTitre) {
        this.formationTitre = formationTitre;
    }

    public String getFormationDescription() {
        return formationDescription;
    }

    public void setFormationDescription(String formationDescription) {
        this.formationDescription = formationDescription;
    }

    public String getFormationLienPhoto() {
        return formationLienPhoto;
    }

    public void setFormationLienPhoto(String formationLienPhoto) {
        this.formationLienPhoto = formationLienPhoto;
    }

    public String getFormationType() {
        return formationType;
    }

    public void setFormationType(String formationType) {
        this.formationType = formationType;
    }

    public Double getFormationDuree() {
        return formationDuree;
    }

    public void setFormationDuree(Double formationDuree) {
        this.formationDuree = formationDuree;
    }

    public String getCollaborateurFullName() {
        if (collaborateurPrenom != null && collaborateurNom != null) {
            return collaborateurPrenom + " " + collaborateurNom;
        }
        return null;
    }

    public boolean isCompleted() {
        return progress != null && progress.compareTo(new BigDecimal("100.00")) >= 0;
    }
}