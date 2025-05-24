package com.competencesplateforme.formationmanagementservice.dto;

public class SupportDTO {

    private Integer id;
    private Integer moduleId;
    private String titre;
    private String description;
    private String type;
    private Double duree;
    private String lien;
    private String downloadUrl;

    // Constructeurs
    public SupportDTO() {
    }

    public SupportDTO(Integer id, Integer moduleId, String titre, String description, String type, Double duree, String lien) {
        this.id = id;
        this.moduleId = moduleId;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.duree = duree;
        this.lien = lien;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDuree() {
        return duree;
    }

    public void setDuree(Double duree) {
        this.duree = duree;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}