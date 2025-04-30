package com.competencesplateforme.formationmanagementservice.dto;

import java.math.BigDecimal;

/**
 * DTO pour afficher un résumé d'une formation avec des statistiques
 */
public class FormationSummaryDTO {

    private Integer id;
    private String titre;
    private String type;
    private String lienPhoto;
    private Double duree;
    private Long nombreModules;
    private Long nombreParticipants;
    private BigDecimal progressionMoyenne;
    private Long nombreCertificats;

    // Constructeurs
    public FormationSummaryDTO() {
    }

    public FormationSummaryDTO(Integer id, String titre, String type, String lienPhoto, Double duree) {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.lienPhoto = lienPhoto;
        this.duree = duree;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLienPhoto() {
        return lienPhoto;
    }

    public void setLienPhoto(String lienPhoto) {
        this.lienPhoto = lienPhoto;
    }

    public Double getDuree() { return duree; }

    public void setDuree(Double duree) { this.duree = duree; }

    public Long getNombreModules() {
        return nombreModules;
    }

    public void setNombreModules(Long nombreModules) {
        this.nombreModules = nombreModules;
    }

    public Long getNombreParticipants() {
        return nombreParticipants;
    }

    public void setNombreParticipants(Long nombreParticipants) {
        this.nombreParticipants = nombreParticipants;
    }

    public BigDecimal getProgressionMoyenne() {
        return progressionMoyenne;
    }

    public void setProgressionMoyenne(BigDecimal progressionMoyenne) {
        this.progressionMoyenne = progressionMoyenne;
    }

    public Long getNombreCertificats() {
        return nombreCertificats;
    }

    public void setNombreCertificats(Long nombreCertificats) {
        this.nombreCertificats = nombreCertificats;
    }

    public String getFormattedProgressionMoyenne() {
        if (progressionMoyenne != null) {
            return String.format("%.1f%%", progressionMoyenne);
        }
        return "0%";
    }
}