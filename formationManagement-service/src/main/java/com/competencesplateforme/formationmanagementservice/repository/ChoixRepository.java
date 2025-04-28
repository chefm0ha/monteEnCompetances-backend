package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Choix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoixRepository extends JpaRepository<Choix, Integer> {

    List<Choix> findByQuestionId(Integer questionId);

    @Query("SELECT c FROM Choix c WHERE c.question.id = :questionId AND c.estCorrect = true")
    List<Choix> findCorrectChoixByQuestionId(Integer questionId);
}