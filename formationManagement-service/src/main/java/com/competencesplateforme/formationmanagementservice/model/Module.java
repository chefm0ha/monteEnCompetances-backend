package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "ordre")
    private Integer ordre = 0;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Support> supports = new HashSet<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Quiz> quizs = new HashSet<>();

    // Constructeurs
    public Module() {
    }

    public Module(String titre, String description) {
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

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
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

    public Set<Support> getSupports() {
        return supports;
    }

    public void setSupports(Set<Support> supports) {
        this.supports = supports;
    }

    public Integer getOrdre() { return ordre; }

    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public void addSupport(Support support) {
        supports.add(support);
        support.setModule(this);
    }

    public void removeSupport(Support support) {
        supports.remove(support);
        support.setModule(null);
    }

    public Set<Quiz> getQuizs() {
        return quizs;
    }

    public void setQuizs(Set<Quiz> quizs) {
        this.quizs = quizs;
    }

    public void addQuiz(Quiz quiz) {
        quizs.add(quiz);
        quiz.setModule(this);
    }

    public void removeQuiz(Quiz quiz) {
        quizs.remove(quiz);
        quiz.setModule(null);
    }
}