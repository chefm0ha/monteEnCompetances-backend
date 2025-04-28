package com.competencesplateforme.formationmanagementservice.dto;

public class ChoixDTO {

    private Integer id;
    private Integer questionId;
    private String contenu;
    private Boolean estCorrect;

    // Constructeurs
    public ChoixDTO() {
    }

    public ChoixDTO(Integer id, Integer questionId, String contenu, Boolean estCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.contenu = contenu;
        this.estCorrect = estCorrect;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Boolean getEstCorrect() {
        return estCorrect;
    }

    public void setEstCorrect(Boolean estCorrect) {
        this.estCorrect = estCorrect;
    }
}