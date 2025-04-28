package com.competencesplateforme.formationmanagementservice.dto;

import java.util.HashSet;
import java.util.Set;

public class ModuleDTO {

    private Integer id;
    private Integer formationId;
    private String titre;
    private String description;
    private Set<SupportDTO> supports = new HashSet<>();
    private Set<QuizDTO> quizs = new HashSet<>();

    // Constructeurs
    public ModuleDTO() {
    }

    public ModuleDTO(Integer id, Integer formationId, String titre, String description) {
        this.id = id;
        this.formationId = formationId;
        this.titre = titre;
        this.description = description;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFormationId() {
        return formationId;
    }

    public void setFormationId(Integer formationId) {
        this.formationId = formationId;
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

    public Set<SupportDTO> getSupports() {
        return supports;
    }

    public void setSupports(Set<SupportDTO> supports) {
        this.supports = supports;
    }

    public Set<QuizDTO> getQuizs() {
        return quizs;
    }

    public void setQuizs(Set<QuizDTO> quizs) {
        this.quizs = quizs;
    }
}