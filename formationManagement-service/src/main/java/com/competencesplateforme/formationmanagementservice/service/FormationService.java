package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.dto.FormationWithModuleCountDTO;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageException;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import com.competencesplateforme.formationmanagementservice.mapper.FormationMapper;
import com.competencesplateforme.formationmanagementservice.model.Formation;
import com.competencesplateforme.formationmanagementservice.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormationService {

    private final FormationRepository formationRepository;
    private final FileStorageService fileStorageService;
    private final FormationMapper formationMapper;


    @Autowired
    public FormationService(FormationRepository formationRepository, FileStorageService fileStorageService, FormationMapper formationMapper
    ) {
        this.formationRepository = formationRepository;
        this.fileStorageService = fileStorageService;
        this.formationMapper = formationMapper;
    }

    @Transactional(readOnly = true)
    public List<Formation> getAllFormationsWithModules() {
        return formationRepository.findAllWithModules();
    }

    /**
     * Récupère toutes les formations
     */
    @Transactional(readOnly = true)
    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    /**
     * Récupère une formation par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Formation> getFormationById(Integer id) {
        return formationRepository.findById(id);
    }

    /**
     * Récupère des formations par type
     */
    @Transactional(readOnly = true)
    public List<Formation> getFormationsByType(String type) {
        return formationRepository.findByType(type);
    }

    /**
     * Recherche des formations par mot-clé
     */
    @Transactional(readOnly = true)
    public List<Formation> searchFormations(String keyword) {
        return formationRepository.searchFormations(keyword);
    }

    /**
     * Crée une nouvelle formation
     */
    @Transactional
    public Formation createFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    /**
     * Crée une nouvelle formation avec une image
     */
    @Transactional
    public Formation createFormationWithImage(Formation formation, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String publicId = fileStorageService.uploadFile(
                        imageFile,
                        "formation_" + System.currentTimeMillis(),
                        "formations"
                );
                formation.setLienPhoto(publicId);
            } catch (IOException e) {
                throw new FileStorageException("Erreur lors du traitement de l'image", e);
            }
        }
        return formationRepository.save(formation);
    }

    /**
     * Met à jour une formation existante
     */
    @Transactional
    public Optional<Formation> updateFormation(Integer id, Formation updatedFormation) {
        return formationRepository.findById(id)
                .map(existingFormation -> {
                    // Mise à jour des propriétés
                    existingFormation.setTitre(updatedFormation.getTitre());
                    existingFormation.setDescription(updatedFormation.getDescription());
                    existingFormation.setType(updatedFormation.getType());

                    // Si un nouveau lien d'image est fourni, l'utiliser
                    if (updatedFormation.getLienPhoto() != null) {
                        // Si l'ancien lien existe, supprimer l'ancienne image
                        if (existingFormation.getLienPhoto() != null) {
                            fileStorageService.deleteFile(existingFormation.getLienPhoto());
                        }
                        existingFormation.setLienPhoto(updatedFormation.getLienPhoto());
                    }

                    return formationRepository.save(existingFormation);
                });
    }

    /**
     * Met à jour une formation existante avec une nouvelle image
     */
    @Transactional
    public Optional<Formation> updateFormationWithImage(Integer id, Formation updatedFormation, MultipartFile imageFile) {
        return formationRepository.findById(id)
                .map(existingFormation -> {
                    // Mise à jour des propriétés
                    existingFormation.setTitre(updatedFormation.getTitre());
                    existingFormation.setDescription(updatedFormation.getDescription());
                    existingFormation.setType(updatedFormation.getType());

                    // Si une nouvelle image est fournie, la traiter
                    if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                            // Si l'ancien lien existe, supprimer l'ancienne image
                            if (existingFormation.getLienPhoto() != null) {
                                fileStorageService.deleteFile(existingFormation.getLienPhoto());
                            }

                            // Uploader la nouvelle image
                            String publicId = fileStorageService.uploadFile(
                                    imageFile,
                                    "formation_" + System.currentTimeMillis(),
                                    "formations"
                            );
                            existingFormation.setLienPhoto(publicId);
                        } catch (IOException e) {
                            throw new FileStorageException("Erreur lors du traitement de la nouvelle image", e);
                        }
                    }

                    return formationRepository.save(existingFormation);
                });
    }

    /**
     * Supprime une formation
     */
    @Transactional
    public boolean deleteFormation(Integer id) {
        return formationRepository.findById(id)
                .map(formation -> {
                    // Si la formation a une image, la supprimer également
                    if (formation.getLienPhoto() != null) {
                        fileStorageService.deleteFile(formation.getLienPhoto());
                    }

                    formationRepository.delete(formation);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<FormationWithModuleCountDTO> getAllFormationsWithModuleCount() {
        List<Object[]> results = formationRepository.findAllWithModuleCount();
        return formationMapper.toFormationWithModuleCountDTOList(results);
    }
}