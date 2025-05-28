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

import java.util.List;
import java.util.Optional;

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
     * Récupère les modules d'une formation
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
     * Cette méthode peut être étendue selon vos besoins spécifiques
     */
    @Transactional
    public boolean reorderModules(Integer formationId, List<Integer> moduleIds) {
        // Cette méthode est un exemple et devrait être adaptée
        // selon la façon dont vous gérez l'ordre des modules
        return formationRepository.findById(formationId)
                .map(formation -> {
                    // Logique de réorganisation à implémenter selon vos besoins
                    return true;
                })
                .orElse(false);
    }
}