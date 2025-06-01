package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.dto.ModuleWithFormationDTO;
import com.competencesplateforme.formationmanagementservice.mapper.ModuleMapper;
import com.competencesplateforme.formationmanagementservice.model.Formation;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.repository.FormationRepository;
import com.competencesplateforme.formationmanagementservice.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final FormationRepository formationRepository;
    private final ModuleMapper moduleMapper;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, FormationRepository formationRepository, ModuleMapper moduleMapper) {
        this.moduleRepository = moduleRepository;
        this.formationRepository = formationRepository;
        this.moduleMapper = moduleMapper;
    }

    /**
     * Récupère tous les modules
     */
    @Transactional(readOnly = true)
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    /**
     * Récupère un module par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Module> getModuleById(Integer id) {
        return moduleRepository.findById(id);
    }

    /**
     * Récupère les modules d'une formation (ordonnés)
     */
    @Transactional(readOnly = true)
    public List<Module> getModulesByFormationId(Integer formationId) {
        return moduleRepository.findByFormationId(formationId);
    }

    /**
     * Recherche des modules par titre
     */
    @Transactional(readOnly = true)
    public List<Module> searchModulesByTitre(String titre) {
        return moduleRepository.findByTitreContainingIgnoreCase(titre);
    }

    /**
     * Récupère tous les modules avec leurs informations de formation et compteurs
     */
    @Transactional(readOnly = true)
    public List<ModuleWithFormationDTO> getAllModulesWithFormationAndCounts() {
        List<Object[]> results = moduleRepository.findAllModulesWithFormationAndCounts();
        return moduleMapper.toModuleWithFormationDTOList(results);
    }

    /**
     * Crée un nouveau module pour une formation
     */
    @Transactional
    public Optional<Module> createModule(Integer formationId, Module module) {
        return formationRepository.findById(formationId)
                .map(formation -> {
                    module.setFormation(formation);
                    // Set order to the next available position
                    if (module.getOrdre() == null) {
                        Integer nextOrder = moduleRepository.getNextOrderValue(formationId);
                        module.setOrdre(nextOrder);
                    }
                    return moduleRepository.save(module);
                });
    }

    /**
     * Met à jour un module existant
     */
    @Transactional
    public Optional<Module> updateModule(Integer id, Module updatedModule) {
        return moduleRepository.findById(id)
                .map(existingModule -> {
                    existingModule.setTitre(updatedModule.getTitre());
                    existingModule.setDescription(updatedModule.getDescription());
                    // Only update order if provided
                    if (updatedModule.getOrdre() != null) {
                        existingModule.setOrdre(updatedModule.getOrdre());
                    }
                    return moduleRepository.save(existingModule);
                });
    }

    /**
     * Supprime un module
     */
    @Transactional
    public boolean deleteModule(Integer id) {
        return moduleRepository.findById(id)
                .map(module -> {
                    moduleRepository.delete(module);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Réorganise l'ordre des modules dans une formation
     */
    @Transactional
    public boolean reorderModules(Integer formationId, List<Integer> moduleIds) {
        // Validate formation exists
        if (!formationRepository.existsById(formationId)) {
            throw new IllegalArgumentException("Formation with ID " + formationId + " not found");
        }

        // Validate all modules belong to the formation
        List<Module> modules = moduleRepository.findByFormationIdAndIdIn(formationId, moduleIds);

        if (modules.size() != moduleIds.size()) {
            throw new IllegalArgumentException("Some module IDs do not belong to the specified formation");
        }

        // Check for duplicates in moduleIds
        Set<Integer> uniqueIds = new HashSet<>(moduleIds);
        if (uniqueIds.size() != moduleIds.size()) {
            throw new IllegalArgumentException("Duplicate module IDs found in the request");
        }

        // Update the order for each module
        try {
            IntStream.range(0, moduleIds.size())
                    .forEach(index -> {
                        Integer moduleId = moduleIds.get(index);
                        Integer newOrder = index + 1; // Start from 1
                        moduleRepository.updateModuleOrder(moduleId, newOrder);
                    });

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to reorder modules: " + e.getMessage(), e);
        }
    }

    /**
     * Valide que tous les modules appartiennent à la formation spécifiée
     */
    @Transactional(readOnly = true)
    public boolean validateModulesBelongToFormation(Integer formationId, List<Integer> moduleIds) {
        List<Module> modules = moduleRepository.findByFormationIdAndIdIn(formationId, moduleIds);
        return modules.size() == moduleIds.size();
    }
}