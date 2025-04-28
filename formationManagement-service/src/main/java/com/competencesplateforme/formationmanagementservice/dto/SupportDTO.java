package com.competencesplateforme.formationmanagementservice.dto;

public class SupportDTO {

    private Integer id;
    private Integer moduleId;
    private String type;
    private String lien;
    private String downloadUrl;

    // Constructeurs
    public SupportDTO() {
    }

    public SupportDTO(Integer id, Integer moduleId, String type, String lien) {
        this.id = id;
        this.moduleId = moduleId;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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