package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.QuizDTO;
import com.competencesplateforme.formationmanagementservice.dto.QuizResultDTO;
import com.competencesplateforme.formationmanagementservice.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QuizMapper {

    private final QuestionMapper questionMapper;

    @Autowired
    public QuizMapper(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public QuizDTO toDTO(Quiz quiz) {
        if (quiz == null) {
            return null;
        }

        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        if (quiz.getModule() != null) {
            dto.setModuleId(quiz.getModule().getId());
        }
        dto.setTitre(quiz.getTitre());
        dto.setSeuilReussite(quiz.getSeuilReussite());

        return dto;
    }

    public QuizDTO toDTOWithQuestions(Quiz quiz) {
        QuizDTO dto = toDTO(quiz);
        if (dto != null && quiz.getQuestions() != null) {
            dto.setQuestions(quiz.getQuestions().stream()
                    .map(questionMapper::toDTOWithChoix)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public Quiz toEntity(QuizDTO dto) {
        if (dto == null) {
            return null;
        }

        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setTitre(dto.getTitre());
        quiz.setSeuilReussite(dto.getSeuilReussite());

        return quiz;
    }

    public List<QuizDTO> toDTOListWithQuestions(List<Quiz> quizzes) {
        if (quizzes == null) {
            return null;
        }

        return quizzes.stream()
                .map(this::toDTOWithQuestions)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(QuizDTO dto, Quiz quiz) {
        if (dto == null || quiz == null) {
            return;
        }

        if (dto.getTitre() != null) {
            quiz.setTitre(dto.getTitre());
        }
        if (dto.getSeuilReussite() != null) {
            quiz.setSeuilReussite(dto.getSeuilReussite());
        }
    }

    public List<QuizDTO> toDTOList(List<Quiz> quizzes) {
        return quizzes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public QuizResultDTO toResultDTO(Quiz quiz, Map<String, Object> resultData) {
        if (quiz == null || resultData == null) {
            return null;
        }

        QuizResultDTO resultDTO = new QuizResultDTO();
        resultDTO.setQuizId(quiz.getId());
        resultDTO.setQuizTitre(quiz.getTitre());

        // Extraire les données du résultat
        resultDTO.setTotalQuestions((Integer) resultData.get("totalQuestions"));
        resultDTO.setCorrectAnswers((Integer) resultData.get("correctAnswers"));

        // Convertir le score en Double si nécessaire
        Object scoreObj = resultData.get("scorePercentage");
        if (scoreObj instanceof Number) {
            resultDTO.setScorePercentage(((Number) scoreObj).doubleValue());
        }

        resultDTO.setIsPassed((Boolean) resultData.get("isPassed"));

        return resultDTO;
    }
}