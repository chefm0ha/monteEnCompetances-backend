package com.competencesplateforme.formationmanagementservice.dto;

import java.util.HashSet;
import java.util.Set;

public class QuizDTO {

    private Integer id;
    private Integer moduleId;
    private String titre;
    private Set<QuestionDTO> questions = new HashSet<>();

    // Constructeurs
    public QuizDTO() {
    }

    public QuizDTO(Integer id, Integer moduleId, String titre) {
        this.id = id;
        this.moduleId = moduleId;
        this.titre = titre;
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

    public Set<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionDTO> questions) {
        this.questions = questions;
    }
}