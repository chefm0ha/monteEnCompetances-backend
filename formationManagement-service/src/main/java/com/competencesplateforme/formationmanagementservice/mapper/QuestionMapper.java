package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.QuestionDTO;
import com.competencesplateforme.formationmanagementservice.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    private final ChoixMapper choixMapper;

    @Autowired
    public QuestionMapper(ChoixMapper choixMapper) {
        this.choixMapper = choixMapper;
    }

    public QuestionDTO toDTO(Question question) {
        if (question == null) {
            return null;
        }

        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        if (question.getQuiz() != null) {
            dto.setQuizId(question.getQuiz().getId());
        }
        dto.setContenu(question.getContenu());

        return dto;
    }

    public QuestionDTO toDTOWithChoix(Question question) {
        QuestionDTO dto = toDTO(question);
        if (dto != null && question.getChoix() != null) {
            dto.setChoix(question.getChoix().stream()
                    .map(choixMapper::toDTO)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public Question toEntity(QuestionDTO dto) {
        if (dto == null) {
            return null;
        }

        Question question = new Question();
        question.setId(dto.getId());
        question.setContenu(dto.getContenu());
        // Le quiz est généralement défini séparément via le service

        return question;
    }

    public void updateEntityFromDTO(QuestionDTO dto, Question question) {
        if (dto == null || question == null) {
            return;
        }

        if (dto.getContenu() != null) {
            question.setContenu(dto.getContenu());
        }
    }

    public List<QuestionDTO> toDTOList(List<Question> questions) {
        return questions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}