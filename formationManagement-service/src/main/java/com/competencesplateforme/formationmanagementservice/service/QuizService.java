package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.model.Choix;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.model.Question;
import com.competencesplateforme.formationmanagementservice.model.Quiz;
import com.competencesplateforme.formationmanagementservice.repository.ChoixRepository;
import com.competencesplateforme.formationmanagementservice.repository.ModuleRepository;
import com.competencesplateforme.formationmanagementservice.repository.QuestionRepository;
import com.competencesplateforme.formationmanagementservice.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final ChoixRepository choixRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository,
                       ModuleRepository moduleRepository,
                       QuestionRepository questionRepository,
                       ChoixRepository choixRepository) {
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.choixRepository = choixRepository;
    }

    /**
     * Récupère tous les quiz
     */
    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    /**
     * Récupère un quiz par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Quiz> getQuizById(Integer id) {
        return quizRepository.findById(id);
    }

    /**
     * Récupère les quiz d'un module
     */
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByModuleId(Integer moduleId) {
        return quizRepository.findByModuleId(moduleId);
    }

    /**
     * Recherche des quiz par titre
     */
    @Transactional(readOnly = true)
    public List<Quiz> searchQuizzesByTitle(String title) {
        return quizRepository.findByTitreContainingIgnoreCase(title);
    }

    /**
     * Crée un nouveau quiz
     */
    @Transactional
    public Optional<Quiz> createQuiz(Integer moduleId, Quiz quiz) {
        return moduleRepository.findById(moduleId)
                .map(module -> {
                    quiz.setModule(module);
                    return quizRepository.save(quiz);
                });
    }

    /**
     * Met à jour un quiz existant
     */
    @Transactional
    public Optional<Quiz> updateQuiz(Integer id, Quiz updatedQuiz) {
        return quizRepository.findById(id)
                .map(existingQuiz -> {
                    existingQuiz.setTitre(updatedQuiz.getTitre());
                    return quizRepository.save(existingQuiz);
                });
    }

    /**
     * Supprime un quiz
     */
    @Transactional
    public boolean deleteQuiz(Integer id) {
        return quizRepository.findById(id)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Ajoute une question à un quiz
     */
    @Transactional
    public Optional<Question> addQuestionToQuiz(Integer quizId, Question question) {
        return quizRepository.findById(quizId)
                .map(quiz -> {
                    question.setQuiz(quiz);
                    return questionRepository.save(question);
                });
    }

    /**
     * Ajoute une liste de choix à une question
     */
    @Transactional
    public List<Choix> addChoicesToQuestion(Integer questionId, List<Choix> choixList) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);

        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();

            // Associer chaque choix à la question et les sauvegarder
            choixList.forEach(choix -> choix.setQuestion(question));
            return choixRepository.saveAll(choixList);
        }

        return List.of(); // Retourne une liste vide si la question n'existe pas
    }

    /**
     * Vérifie les réponses d'un utilisateur et calcule le score
     */
    @Transactional(readOnly = true)
    public Map<String, Object> evaluateQuizAnswers(Integer quizId, Map<Integer, List<Integer>> userAnswers) {
        // userAnswers est une map de questionId -> liste des choixId sélectionnés par l'utilisateur

        // Récupérer le quiz avec toutes ses questions et tous les choix corrects
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        Set<Question> questions = quiz.getQuestions();

        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // Pour chaque question, vérifier si les réponses de l'utilisateur sont correctes
        for (Question question : questions) {
            List<Integer> userChoices = userAnswers.getOrDefault(question.getId(), List.of());
            List<Choix> correctChoices = choixRepository.findCorrectChoixByQuestionId(question.getId());

            Set<Integer> correctChoicesIds = correctChoices.stream()
                    .map(Choix::getId)
                    .collect(Collectors.toSet());

            Set<Integer> userChoicesSet = userChoices.stream()
                    .collect(Collectors.toSet());

            // La réponse est correcte si l'utilisateur a sélectionné exactement tous les choix corrects
            if (userChoicesSet.equals(correctChoicesIds)) {
                correctAnswers++;
            }
        }

        double scorePercentage = totalQuestions > 0
                ? (double) correctAnswers / totalQuestions * 100
                : 0;

        // Créer et retourner le résultat
        return Map.of(
                "totalQuestions", totalQuestions,
                "correctAnswers", correctAnswers,
                "scorePercentage", scorePercentage,
                "isPassed", scorePercentage >= 70 // Considérer le quiz réussi si le score est >= 70%
        );
    }
}