package com.competencesplateforme.formationmanagementservice.dto;

import java.util.List;

public class QuizWithQuestionsDTO {
    private Integer id;
    private String titre;
    private String description;
    private Integer seuilReussite;
    private Integer moduleId;
    private List<QuestionWithChoicesDTO> questions;

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

    public Integer getSeuilReussite() {
        return seuilReussite;
    }

    public void setSeuilReussite(Integer seuilReussite) {
        this.seuilReussite = seuilReussite;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public List<QuestionWithChoicesDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionWithChoicesDTO> questions) {
        this.questions = questions;
    }
}
