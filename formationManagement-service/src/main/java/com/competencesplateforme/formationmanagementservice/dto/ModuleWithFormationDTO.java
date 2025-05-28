package com.competencesplateforme.formationmanagementservice.dto;

public class ModuleWithFormationDTO {
    private Integer id;
    private String titre;
    private String description;
    private Integer formationId;
    private String formationTitre;
    private Integer nombreSupports;
    // Constructeurs
    public ModuleWithFormationDTO() {
    }

    public ModuleWithFormationDTO(Integer id, String titre, String description,
                                  Integer formationId, String formationTitre,
                                  Integer nombreSupports) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.formationId = formationId;
        this.formationTitre = formationTitre;
        this.nombreSupports = nombreSupports;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFormationId() {
        return formationId;
    }

    public void setFormationId(Integer formationId) {
        this.formationId = formationId;
    }

    public String getFormationTitre() {
        return formationTitre;
    }

    public void setFormationTitre(String formationTitre) {
        this.formationTitre = formationTitre;
    }

    public Integer getNombreSupports() {
        return nombreSupports;
    }

    public void setNombreSupports(Integer nombreSupports) {
        this.nombreSupports = nombreSupports;
    }
}