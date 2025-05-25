package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quizs")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(columnDefinition = "TEXT")
    private String titre;

    @Column(name = "seuil_reussite")
    private Integer seuilReussite = 70; // Default to 70%

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions = new HashSet<>();

    // Constructeurs
    public Quiz() {
    }

    public Quiz(String titre) {
        this.titre = titre;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Integer getSeuilReussite() { return seuilReussite; }

    public void setSeuilReussite(Integer seuilReussite) { this.seuilReussite = seuilReussite; }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }
}