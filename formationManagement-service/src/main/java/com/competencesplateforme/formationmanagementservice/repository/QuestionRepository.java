package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findByQuizId(Integer quizId);

    List<Question> findByContenuContainingIgnoreCase(String contenu);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Integer quizId);
}