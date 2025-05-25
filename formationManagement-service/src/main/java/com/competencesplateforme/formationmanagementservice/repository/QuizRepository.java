package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    List<Quiz> findByModuleId(Integer moduleId);

    List<Quiz> findByTitreContainingIgnoreCase(String titre);

    @Query("SELECT q FROM Quiz q " +
            "LEFT JOIN FETCH q.questions qu " +
            "LEFT JOIN FETCH qu.choix " +
            "WHERE q.module.id = :moduleId")
    List<Quiz> findByModuleIdWithQuestionsAndChoices(@Param("moduleId") Integer moduleId);
}