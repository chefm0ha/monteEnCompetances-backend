package com.competencesplateforme.collaborateurmanagementservice.repository;

import com.competencesplateforme.collaborateurmanagementservice.model.Collaborateur;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollaborateurRepository extends JpaRepository<Collaborateur, UUID> {
    // Vous pouvez ajouter des méthodes de recherche spécifiques
    Optional<Collaborateur> findByEmail(String email);
    List<Collaborateur> findByPoste(String poste);

    @Query("SELECT c.poste, COUNT(c) FROM Collaborateur c GROUP BY c.poste ORDER BY COUNT(c) DESC")
    List<Object[]> getCollaborateurCountByPosition();

    @Query("SELECT c FROM Collaborateur c ORDER BY c.id DESC")
    List<Collaborateur> findTop5ByOrderByIdDesc(Pageable pageable);

    // For convenience method
    default List<Collaborateur> findTop5ByOrderByIdDesc() {
        return findTop5ByOrderByIdDesc((Pageable) PageRequest.of(0, 5));
    }
}