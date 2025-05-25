package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.dto.QuestionDTO;
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

    public List<Quiz> getQuizzesByModuleIdWithQuestions(Integer moduleId) {
        return quizRepository.findByModuleIdWithQuestionsAndChoices(moduleId);
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
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        Set<Question> questions = quiz.getQuestions();

        int totalQuestions = questions.size();
        int correctAnswers = 0;

        for (Question question : questions) {
            List<Integer> userChoices = userAnswers.getOrDefault(question.getId(), List.of());
            List<Choix> correctChoices = choixRepository.findCorrectChoixByQuestionId(question.getId());

            Set<Integer> correctChoicesIds = correctChoices.stream()
                    .map(Choix::getId)
                    .collect(Collectors.toSet());

            Set<Integer> userChoicesSet = userChoices.stream()
                    .collect(Collectors.toSet());

            if (userChoicesSet.equals(correctChoicesIds)) {
                correctAnswers++;
            }
        }

        double scorePercentage = totalQuestions > 0
                ? (double) correctAnswers / totalQuestions * 100
                : 0;

        // Use the quiz's own threshold instead of hardcoded 70%
        Integer threshold = quiz.getSeuilReussite() != null ? quiz.getSeuilReussite() : 70;

        return Map.of(
                "totalQuestions", totalQuestions,
                "correctAnswers", correctAnswers,
                "scorePercentage", scorePercentage,
                "isPassed", scorePercentage >= threshold,
                "threshold", threshold
        );
    }

    /**
     * Met à jour un quiz complet avec toutes ses données
     */
    @Transactional
    public Optional<Quiz> updateCompleteQuiz(Integer id, Quiz updatedQuiz) {
        return quizRepository.findById(id)
                .map(existingQuiz -> {
                    existingQuiz.setTitre(updatedQuiz.getTitre());
                    existingQuiz.setSeuilReussite(updatedQuiz.getSeuilReussite());
                    return quizRepository.save(existingQuiz);
                });
    }

    /**
     * Crée un quiz complet avec ses questions et choix
     */
    @Transactional
    public Optional<Quiz> createCompleteQuiz(Integer moduleId, Quiz quiz, Set<QuestionDTO> questionsDTO) {
        return moduleRepository.findById(moduleId)
                .map(module -> {
                    quiz.setModule(module);
                    Quiz savedQuiz = quizRepository.save(quiz);

                    // Créer les questions et leurs choix
                    if (questionsDTO != null && !questionsDTO.isEmpty()) {
                        for (QuestionDTO questionDTO : questionsDTO) {
                            Question question = new Question(questionDTO.getContenu());
                            question.setQuiz(savedQuiz);
                            Question savedQuestion = questionRepository.save(question);

                            // Créer les choix pour cette question
                            if (questionDTO.getChoix() != null && !questionDTO.getChoix().isEmpty()) {
                                List<Choix> choixList = questionDTO.getChoix().stream()
                                        .map(choixDTO -> new Choix(choixDTO.getContenu(), choixDTO.getEstCorrect()))
                                        .collect(Collectors.toList());

                                choixList.forEach(choix -> choix.setQuestion(savedQuestion));
                                choixRepository.saveAll(choixList);
                            }
                        }
                    }

                    return savedQuiz;
                });
    }

    /**
     * Met à jour un quiz complet avec ses questions et choix
     */
    @Transactional
    public Optional<Quiz> updateCompleteQuiz(Integer id, Quiz updatedQuiz, Set<QuestionDTO> questionsDTO) {
        return quizRepository.findById(id)
                .map(existingQuiz -> {
                    // Mettre à jour les propriétés du quiz
                    existingQuiz.setTitre(updatedQuiz.getTitre());
                    existingQuiz.setSeuilReussite(updatedQuiz.getSeuilReussite());

                    Quiz savedQuiz = quizRepository.save(existingQuiz);

                    // Pour une mise à jour complète, on pourrait supprimer et recréer les questions
                    // Ou implémenter une logique plus sophistiquée de mise à jour
                    // Cette approche simple supprime tout et recrée
                    if (questionsDTO != null) {
                        // Supprimer les anciennes questions (cascade supprimera les choix)
                        questionRepository.deleteByQuizId(id);

                        // Créer les nouvelles questions
                        for (QuestionDTO questionDTO : questionsDTO) {
                            Question question = new Question(questionDTO.getContenu());
                            question.setQuiz(savedQuiz);
                            Question savedQuestion = questionRepository.save(question);

                            // Créer les choix pour cette question
                            if (questionDTO.getChoix() != null && !questionDTO.getChoix().isEmpty()) {
                                List<Choix> choixList = questionDTO.getChoix().stream()
                                        .map(choixDTO -> new Choix(choixDTO.getContenu(), choixDTO.getEstCorrect()))
                                        .collect(Collectors.toList());

                                choixList.forEach(choix -> choix.setQuestion(savedQuestion));
                                choixRepository.saveAll(choixList);
                            }
                        }
                    }

                    return savedQuiz;
                });
    }
}