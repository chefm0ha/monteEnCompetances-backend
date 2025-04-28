package com.competencesplateforme.formationmanagementservice.dto;

public class QuizResultDTO {

    private Integer quizId;
    private String quizTitre;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double scorePercentage;
    private Boolean isPassed;

    // Constructeurs
    public QuizResultDTO() {
    }

    public QuizResultDTO(Integer quizId, String quizTitre, Integer totalQuestions, Integer correctAnswers,
                         Double scorePercentage, Boolean isPassed) {
        this.quizId = quizId;
        this.quizTitre = quizTitre;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.scorePercentage = scorePercentage;
        this.isPassed = isPassed;
    }

    // Getters et Setters
    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitre() {
        return quizTitre;
    }

    public void setQuizTitre(String quizTitre) {
        this.quizTitre = quizTitre;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(Double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String getFormattedScore() {
        return String.format("%.1f%%", scorePercentage);
    }

    public String getResultMessage() {
        return isPassed ? "Félicitations, vous avez réussi le quiz !" : "Vous n'avez pas atteint le score minimum requis.";
    }
}