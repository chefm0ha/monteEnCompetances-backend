package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.model.Question;
import com.competencesplateforme.formationmanagementservice.model.Quiz;
import com.competencesplateforme.formationmanagementservice.repository.QuestionRepository;
import com.competencesplateforme.formationmanagementservice.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * Récupère toutes les questions
     */
    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * Récupère une question par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Question> getQuestionById(Integer id) {
        return questionRepository.findById(id);
    }

    /**
     * Récupère les questions d'un quiz
     */
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByQuizId(Integer quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    /**
     * Recherche des questions par contenu
     */
    @Transactional(readOnly = true)
    public List<Question> searchQuestionsByContent(String content) {
        return questionRepository.findByContenuContainingIgnoreCase(content);
    }

    /**
     * Crée une nouvelle question
     */
    @Transactional
    public Optional<Question> createQuestion(Integer quizId, Question question) {
        return quizRepository.findById(quizId)
                .map(quiz -> {
                    question.setQuiz(quiz);
                    return questionRepository.save(question);
                });
    }

    /**
     * Met à jour une question existante
     */
    @Transactional
    public Optional<Question> updateQuestion(Integer id, Question updatedQuestion) {
        return questionRepository.findById(id)
                .map(existingQuestion -> {
                    existingQuestion.setContenu(updatedQuestion.getContenu());
                    return questionRepository.save(existingQuestion);
                });
    }

    /**
     * Supprime une question
     */
    @Transactional
    public boolean deleteQuestion(Integer id) {
        return questionRepository.findById(id)
                .map(question -> {
                    questionRepository.delete(question);
                    return true;
                })
                .orElse(false);
    }
}