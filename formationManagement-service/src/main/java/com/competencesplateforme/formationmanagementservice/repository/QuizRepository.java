package com.competencesplateforme.formationmanagementservice.repository;

import com.competencesplateforme.formationmanagementservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    List<Quiz> findByModuleId(Integer moduleId);

    List<Quiz> findByTitreContainingIgnoreCase(String titre);
}