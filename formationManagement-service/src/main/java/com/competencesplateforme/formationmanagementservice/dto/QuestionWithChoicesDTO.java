package com.competencesplateforme.formationmanagementservice.dto;

import java.util.List;

public class QuestionWithChoicesDTO {
    private Integer id;
    private String contenu;
    private List<ChoixDTO> choices;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public List<ChoixDTO> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoixDTO> choices) {
        this.choices = choices;
    }
}
