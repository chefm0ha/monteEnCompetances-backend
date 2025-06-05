// ProgressTrackingService.java
package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.model.*;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProgressTrackingService {

    private final CollaborateurSupportProgressRepository supportProgressRepository;
    private final CollaborateurModuleProgressRepository moduleProgressRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final SupportRepository supportRepository;
    private final ModuleRepository moduleRepository;
    private final FormationRepository formationRepository;

    @Autowired
    public ProgressTrackingService(
            CollaborateurSupportProgressRepository supportProgressRepository,
            CollaborateurModuleProgressRepository moduleProgressRepository,
            CollaborateurRepository collaborateurRepository,
            SupportRepository supportRepository,
            ModuleRepository moduleRepository,
            FormationRepository formationRepository) {
        this.supportProgressRepository = supportProgressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.supportRepository = supportRepository;
        this.moduleRepository = moduleRepository;
        this.formationRepository = formationRepository;
    }

    /**
     * Mark a support as seen by a collaborateur
     */
    @Transactional
    public boolean markSupportAsSeen(UUID collaborateurId, Integer supportId) {
        // Check if already seen
        if (supportProgressRepository.existsByCollaborateurIdAndSupportId(collaborateurId, supportId)) {
            return false; // Already seen
        }

        Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findById(collaborateurId);
        Optional<Support> supportOpt = supportRepository.findById(supportId);

        if (collaborateurOpt.isPresent() && supportOpt.isPresent()) {
            CollaborateurSupportProgress progress = new CollaborateurSupportProgress(
                    collaborateurOpt.get(),
                    supportOpt.get()
            );
            supportProgressRepository.save(progress);
            return true;
        }
        return false;
    }

    /**
     * Check if a support has been seen by a collaborateur
     */
    public boolean isSupportSeen(UUID collaborateurId, Integer supportId) {
        return supportProgressRepository.existsByCollaborateurIdAndSupportId(collaborateurId, supportId);
    }

    /**
     * Get all seen supports for a collaborateur in a specific module
     */
    public List<CollaborateurSupportProgress> getSeenSupportsInModule(UUID collaborateurId, Integer moduleId) {
        return supportProgressRepository.findByCollaborateurIdAndModuleId(collaborateurId, moduleId);
    }

    /**
     * Check if all supports in a module have been seen
     */
    public boolean areAllSupportsSeenInModule(UUID collaborateurId, Integer moduleId) {
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            return false;
        }

        Module module = moduleOpt.get();
        long totalSupports = module.getSupports().size();
        long seenSupports = supportProgressRepository.countSeenSupportsByCollaborateurAndModule(collaborateurId, moduleId);

        return totalSupports > 0 && seenSupports >= totalSupports;
    }

    /**
     * Complete a module after quiz success
     */
    @Transactional
    public boolean completeModule(UUID collaborateurId, Integer moduleId, BigDecimal quizScore) {
        Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findById(collaborateurId);
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);

        if (collaborateurOpt.isEmpty() || moduleOpt.isEmpty()) {
            return false;
        }

        // Check if all supports have been seen
        if (!areAllSupportsSeenInModule(collaborateurId, moduleId)) {
            return false; // Cannot complete module without seeing all supports
        }

        // Check if module progress already exists
        Optional<CollaborateurModuleProgress> existingProgress =
                moduleProgressRepository.findByCollaborateurIdAndModuleId(collaborateurId, moduleId);

        CollaborateurModuleProgress moduleProgress;
        if (existingProgress.isPresent()) {
            moduleProgress = existingProgress.get();
        } else {
            moduleProgress = new CollaborateurModuleProgress(collaborateurOpt.get(), moduleOpt.get());
        }

        moduleProgress.markAsCompleted(quizScore);
        moduleProgressRepository.save(moduleProgress);
        return true;
    }

    /**
     * Check if a module is completed
     */
    public boolean isModuleCompleted(UUID collaborateurId, Integer moduleId) {
        return moduleProgressRepository.existsByCollaborateurIdAndModuleIdAndIsCompletedTrue(collaborateurId, moduleId);
    }

    /**
     * Check if a module is unlocked (available to start)
     * A module is unlocked if it's the first module or if the previous module is completed
     */
    public boolean isModuleUnlocked(UUID collaborateurId, Integer moduleId) {
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            return false;
        }

        Module module = moduleOpt.get();
        Integer formationId = module.getFormation().getId();

        // Get all modules in this formation ordered by module_order
        List<Module> allModules = moduleRepository.findByFormationIdOrderByModuleOrder(formationId);

        if (allModules.isEmpty()) {
            return false;
        }

        // First module is always unlocked
        if (allModules.get(0).getId().equals(moduleId)) {
            return true;
        }

        // Find the previous module
        for (int i = 1; i < allModules.size(); i++) {
            if (allModules.get(i).getId().equals(moduleId)) {
                Module previousModule = allModules.get(i - 1);
                return isModuleCompleted(collaborateurId, previousModule.getId());
            }
        }

        return false;
    }

    /**
     * Get next unlocked module in a formation
     */
    public Optional<Module> getNextUnlockedModule(UUID collaborateurId, Integer formationId) {
        List<Module> allModules = moduleRepository.findByFormationIdOrderByModuleOrder(formationId);

        for (Module module : allModules) {
            if (!isModuleCompleted(collaborateurId, module.getId()) &&
                    isModuleUnlocked(collaborateurId, module.getId())) {
                return Optional.of(module);
            }
        }

        return Optional.empty(); // All modules completed or none available
    }

    /**
     * Get module progress for a collaborateur in a formation
     */
    public List<CollaborateurModuleProgress> getModuleProgressInFormation(UUID collaborateurId, Integer formationId) {
        return moduleProgressRepository.findByCollaborateurIdAndFormationIdOrderByModuleOrder(collaborateurId, formationId);
    }

    /**
     * Calculate overall progress percentage for a formation
     */
    public BigDecimal calculateFormationProgress(UUID collaborateurId, Integer formationId) {
        List<Module> allModules = moduleRepository.findByFormationIdOrderByModuleOrder(formationId);
        if (allModules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long completedModules = allModules.stream()
                .mapToLong(module -> isModuleCompleted(collaborateurId, module.getId()) ? 1 : 0)
                .sum();

        return BigDecimal.valueOf((completedModules * 100.0) / allModules.size())
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}