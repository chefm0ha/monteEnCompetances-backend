package com.competencesplateforme.formationmanagementservice.dto;

import java.util.HashSet;
import java.util.Set;

public class FormationDTO {

    private Integer id;
    private String titre;
    private String description;
    private String type;
    private String lienPhoto;
    private Double duree;
    private Set<ModuleDTO> modules = new HashSet<>();

    // Constructeurs
    public FormationDTO() {
    }

    public FormationDTO(Integer id, String titre, String description, String type, String lienPhoto, Double duree) {
        this.id = id;
        this.titre = titre;
        this.description = description;
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

    public String getLienPhoto() {
        return lienPhoto;
    }

    public void setLienPhoto(String lienPhoto) {
        this.lienPhoto = lienPhoto;
    }

    public Set<ModuleDTO> getModules() {
        return modules;
    }

    public void setModules(Set<ModuleDTO> modules) {
        this.modules = modules;
    }

    public Double getDuree() { return duree; }

    public void setDuree(Double duree) { this.duree = duree; }

    @Override
    public String toString() {
        return "FormationDTO{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", lienPhoto='" + lienPhoto + '\'' +
                ", duree=" + duree +
                ", modules=" + modules +
                '}';
    }
}