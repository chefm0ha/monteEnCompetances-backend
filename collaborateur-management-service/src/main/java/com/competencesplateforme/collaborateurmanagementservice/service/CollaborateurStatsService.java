package com.competencesplateforme.collaborateurmanagementservice.service;

import com.competencesplateforme.collaborateurmanagementservice.dto.CollaborateurStatsDTO;
import com.competencesplateforme.collaborateurmanagementservice.model.Collaborateur;
import com.competencesplateforme.collaborateurmanagementservice.repository.CollaborateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollaborateurStatsService {

    private final CollaborateurRepository collaborateurRepository;

    @Autowired
    public CollaborateurStatsService(CollaborateurRepository collaborateurRepository) {
        this.collaborateurRepository = collaborateurRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCollaborateursStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total collaborators
        long totalCollaborateurs = collaborateurRepository.count();
        stats.put("totalCollaborateurs", totalCollaborateurs);

        // Collaborators by position
        Map<String, Long> postes = getCollaborateursByPosition();
        stats.put("postes", postes);

        // Recent collaborators
        List<CollaborateurStatsDTO> collaborateursRecents = getRecentCollaborateurs();
        stats.put("collaborateursRecents", collaborateursRecents);

        return stats;
    }

    private Map<String, Long> getCollaborateursByPosition() {
        List<Object[]> positionData = collaborateurRepository.getCollaborateurCountByPosition();

        return positionData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0], // position
                        row -> (Long) row[1],   // count
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    private List<CollaborateurStatsDTO> getRecentCollaborateurs() {
        List<Collaborateur> recentCollaborateurs = collaborateurRepository.findTop5ByOrderByIdDesc();

        return recentCollaborateurs.stream()
                .map(collaborateur -> new CollaborateurStatsDTO(
                        collaborateur.getId(),
                        collaborateur.getFirstName(),
                        collaborateur.getLastName(),
                        collaborateur.getEmail(),
                        collaborateur.getPoste(),
                        // For now, we'll set progression to null since it would require
                        // calling the formation service. You can implement this later
                        // by making a REST call to the formation service
                        null
                ))
                .collect(Collectors.toList());
    }
}