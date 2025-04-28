package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "choix")
public class Choix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "est_correct", nullable = false)
    private Boolean estCorrect;

    // Constructeurs
    public Choix() {
    }

    public Choix(String contenu, Boolean estCorrect) {
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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