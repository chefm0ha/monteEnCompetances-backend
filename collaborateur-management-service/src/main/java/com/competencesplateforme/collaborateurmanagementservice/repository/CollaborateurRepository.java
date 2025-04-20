package com.competencesplateforme.collaborateurmanagementservice.repository;

import com.competencesplateforme.collaborateurmanagementservice.model.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollaborateurRepository extends JpaRepository<Collaborateur, UUID> {
    // Vous pouvez ajouter des méthodes de recherche spécifiques
    Optional<Collaborateur> findByEmail(String email);
    List<Collaborateur> findByPoste(String poste);
}