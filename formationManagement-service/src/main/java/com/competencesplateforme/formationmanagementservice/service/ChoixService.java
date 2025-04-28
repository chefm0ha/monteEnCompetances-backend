package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.model.Choix;
import com.competencesplateforme.formationmanagementservice.model.Question;
import com.competencesplateforme.formationmanagementservice.repository.ChoixRepository;
import com.competencesplateforme.formationmanagementservice.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChoixService {

    private final ChoixRepository choixRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public ChoixService(ChoixRepository choixRepository, QuestionRepository questionRepository) {
        this.choixRepository = choixRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * Récupère tous les choix
     */
    @Transactional(readOnly = true)
    public List<Choix> getAllChoix() {
        return choixRepository.findAll();
    }

    /**
     * Récupère un choix par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Choix> getChoixById(Integer id) {
        return choixRepository.findById(id);
    }

    /**
     * Récupère les choix d'une question
     */
    @Transactional(readOnly = true)
    public List<Choix> getChoixByQuestionId(Integer questionId) {
        return choixRepository.findByQuestionId(questionId);
    }

    /**
     * Récupère les choix corrects d'une question
     */
    @Transactional(readOnly = true)
    public List<Choix> getCorrectChoixByQuestionId(Integer questionId) {
        return choixRepository.findCorrectChoixByQuestionId(questionId);
    }

    /**
     * Crée un nouveau choix
     */
    @Transactional
    public Optional<Choix> createChoix(Integer questionId, Choix choix) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    choix.setQuestion(question);
                    return choixRepository.save(choix);
                });
    }

    /**
     * Crée plusieurs choix pour une question
     */
    @Transactional
    public List<Choix> createMultipleChoix(Integer questionId, List<Choix> choixList) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);

        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();

            // Associer chaque choix à la question
            choixList.forEach(choix -> choix.setQuestion(question));

            return choixRepository.saveAll(choixList);
        }

        return List.of(); // Retourne une liste vide si la question n'existe pas
    }

    /**
     * Met à jour un choix existant
     */
    @Transactional
    public Optional<Choix> updateChoix(Integer id, Choix updatedChoix) {
        return choixRepository.findById(id)
                .map(existingChoix -> {
                    existingChoix.setContenu(updatedChoix.getContenu());
                    existingChoix.setEstCorrect(updatedChoix.getEstCorrect());
                    return choixRepository.save(existingChoix);
                });
    }

    /**
     * Supprime un choix
     */
    @Transactional
    public boolean deleteChoix(Integer id) {
        return choixRepository.findById(id)
                .map(choix -> {
                    choixRepository.delete(choix);
                    return true;
                })
                .orElse(false);
    }
}