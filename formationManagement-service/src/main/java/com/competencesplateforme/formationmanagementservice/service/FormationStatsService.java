package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.dto.FormationStatsDTO;
import com.competencesplateforme.formationmanagementservice.model.Formation;
import com.competencesplateforme.formationmanagementservice.repository.CollaborateurFormationRepository;
import com.competencesplateforme.formationmanagementservice.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormationStatsService {

    private final FormationRepository formationRepository;
    private final CollaborateurFormationRepository collaborateurFormationRepository;

    @Autowired
    public FormationStatsService(FormationRepository formationRepository,
                                 CollaborateurFormationRepository collaborateurFormationRepository) {
        this.formationRepository = formationRepository;
        this.collaborateurFormationRepository = collaborateurFormationRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFormationsStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total formations
        long totalFormations = formationRepository.count();
        stats.put("totalFormations", totalFormations);

        // Formation completion statistics
        Map<String, Long> completionStats = getFormationCompletionStats();
        stats.put("completedFormations", completionStats.get("completed"));
        stats.put("inProgressFormations", completionStats.get("inProgress"));
        stats.put("notStartedFormations", completionStats.get("notStarted"));

        // Formations by category
        Map<String, Long> formationsParCategorie = getFormationsByCategory();
        stats.put("formationsParCategorie", formationsParCategorie);

        // Recent formations with completion counts
        List<FormationStatsDTO> formationsRecentes = getRecentFormationsWithStats();
        stats.put("formationsRecentes", formationsRecentes);

        return stats;
    }

    private Map<String, Long> getFormationCompletionStats() {
        // Get all collaborateur-formation relationships
        List<Object[]> completionData = collaborateurFormationRepository.getFormationCompletionStats();

        long completed = 0;
        long inProgress = 0;
        long notStarted = 0;

        // Calculate total possible enrollments (total formations * total collaborators)
        long totalFormations = formationRepository.count();
        long totalCollaborateurs = collaborateurFormationRepository.getTotalCollaborateursCount();
        long totalPossibleEnrollments = totalFormations * totalCollaborateurs;
        long totalEnrollments = collaborateurFormationRepository.count();

        // Not started = possible enrollments - actual enrollments
        notStarted = totalPossibleEnrollments - totalEnrollments;

        // Count completed and in-progress from actual enrollments
        for (Object[] row : completionData) {
            BigDecimal progress = (BigDecimal) row[0];
            Long count = (Long) row[1];

            if (progress.compareTo(new BigDecimal("100.00")) >= 0) {
                completed += count;
            } else {
                inProgress += count;
            }
        }

        Map<String, Long> stats = new HashMap<>();
        stats.put("completed", completed);
        stats.put("inProgress", inProgress);
        stats.put("notStarted", notStarted);

        return stats;
    }

    private Map<String, Long> getFormationsByCategory() {
        List<Object[]> categoryData = formationRepository.getFormationCountByCategory();

        return categoryData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0], // category/type
                        row -> (Long) row[1],   // count
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    private List<FormationStatsDTO> getRecentFormationsWithStats() {
        List<Formation> recentFormations = formationRepository.findTop5ByOrderByIdDesc();

        return recentFormations.stream()
                .map(formation -> {
                    Long completions = collaborateurFormationRepository.countCompletedByFormationId(formation.getId());
                    return new FormationStatsDTO(
                            formation.getId(),
                            formation.getTitre(),
                            completions != null ? completions : 0L
                    );
                })
                .collect(Collectors.toList());
    }
}