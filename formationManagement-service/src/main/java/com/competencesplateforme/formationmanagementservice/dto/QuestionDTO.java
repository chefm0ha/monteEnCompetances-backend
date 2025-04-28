package com.competencesplateforme.formationmanagementservice.dto;

import java.util.HashSet;
import java.util.Set;

public class QuestionDTO {

    private Integer id;
    private Integer quizId;
    private String contenu;
    private Set<ChoixDTO> choix = new HashSet<>();

    // Constructeurs
    public QuestionDTO() {
    }

    public QuestionDTO(Integer id, Integer quizId, String contenu) {
        this.id = id;
        this.quizId = quizId;
        this.contenu = contenu;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Set<ChoixDTO> getChoix() {
        return choix;
    }

    public void setChoix(Set<ChoixDTO> choix) {
        this.choix = choix;
    }
}