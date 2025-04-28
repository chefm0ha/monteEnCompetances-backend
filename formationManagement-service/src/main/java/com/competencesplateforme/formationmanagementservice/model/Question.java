package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Choix> choix = new HashSet<>();

    // Constructeurs
    public Question() {
    }

    public Question(String contenu) {
        this.contenu = contenu;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Set<Choix> getChoix() {
        return choix;
    }

    public void setChoix(Set<Choix> choix) {
        this.choix = choix;
    }

    public void addChoix(Choix choixItem) {
        choix.add(choixItem);
        choixItem.setQuestion(this);
    }

    public void removeChoix(Choix choixItem) {
        choix.remove(choixItem);
        choixItem.setQuestion(null);
    }
}