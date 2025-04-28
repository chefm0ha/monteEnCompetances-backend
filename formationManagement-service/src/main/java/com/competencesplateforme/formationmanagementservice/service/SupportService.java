package com.competencesplateforme.formationmanagementservice.service;

import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageException;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.model.Support;
import com.competencesplateforme.formationmanagementservice.repository.ModuleRepository;
import com.competencesplateforme.formationmanagementservice.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SupportService {

    private final SupportRepository supportRepository;
    private final ModuleRepository moduleRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public SupportService(SupportRepository supportRepository,
                          ModuleRepository moduleRepository,
                          FileStorageService fileStorageService) {
        this.supportRepository = supportRepository;
        this.moduleRepository = moduleRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Récupère tous les supports
     */
    @Transactional(readOnly = true)
    public List<Support> getAllSupports() {
        return supportRepository.findAll();
    }

    /**
     * Récupère un support par son ID
     */
    @Transactional(readOnly = true)
    public Optional<Support> getSupportById(Integer id) {
        return supportRepository.findById(id);
    }

    /**
     * Récupère les supports d'un module
     */
    @Transactional(readOnly = true)
    public List<Support> getSupportsByModuleId(Integer moduleId) {
        return supportRepository.findByModuleId(moduleId);
    }

    /**
     * Récupère les supports par type
     */
    @Transactional(readOnly = true)
    public List<Support> getSupportsByType(String type) {
        return supportRepository.findByType(type);
    }

    /**
     * Crée un nouveau support avec fichier
     */
    @Transactional
    public Optional<Support> createSupportWithFile(Integer moduleId, Support support, MultipartFile file) {
        return moduleRepository.findById(moduleId)
                .map(module -> {
                    support.setModule(module);

                    // Si un fichier est fourni, le traiter
                    if (file != null && !file.isEmpty()) {
                        try {
                            String folderPath = "supports/" + support.getType().toLowerCase();
                            String fileName = "support_" + moduleId + "_" + System.currentTimeMillis();

                            String publicId = fileStorageService.uploadFile(file, fileName, folderPath);
                            support.setLien(publicId);
                        } catch (IOException e) {
                            throw new FileStorageException("Erreur lors du traitement du fichier de support", e);
                        }
                    }

                    return supportRepository.save(support);
                });
    }

    /**
     * Met à jour un support existant
     */
    @Transactional
    public Optional<Support> updateSupport(Integer id, Support updatedSupport) {
        return supportRepository.findById(id)
                .map(existingSupport -> {
                    existingSupport.setType(updatedSupport.getType());

                    // Si un nouveau lien est fourni, mettre à jour
                    if (updatedSupport.getLien() != null) {
                        existingSupport.setLien(updatedSupport.getLien());
                    }

                    return supportRepository.save(existingSupport);
                });
    }

    /**
     * Met à jour un support existant avec un nouveau fichier
     */
    @Transactional
    public Optional<Support> updateSupportWithFile(Integer id, Support updatedSupport, MultipartFile file) {
        return supportRepository.findById(id)
                .map(existingSupport -> {
                    existingSupport.setType(updatedSupport.getType());

                    // Si un nouveau fichier est fourni, le traiter
                    if (file != null && !file.isEmpty()) {
                        try {
                            // Si l'ancien lien existe, supprimer l'ancien fichier
                            if (existingSupport.getLien() != null) {
                                fileStorageService.deleteFile(existingSupport.getLien());
                            }

                            String folderPath = "supports/" + existingSupport.getType().toLowerCase();
                            String fileName = "support_" + existingSupport.getModule().getId() + "_" + System.currentTimeMillis();

                            String publicId = fileStorageService.uploadFile(file, fileName, folderPath);
                            existingSupport.setLien(publicId);
                        } catch (IOException e) {
                            throw new FileStorageException("Erreur lors du traitement du nouveau fichier de support", e);
                        }
                    }

                    return supportRepository.save(existingSupport);
                });
    }

    /**
     * Supprime un support
     */
    @Transactional
    public boolean deleteSupport(Integer id) {
        return supportRepository.findById(id)
                .map(support -> {
                    // Si le support a un fichier, le supprimer également
                    if (support.getLien() != null) {
                        fileStorageService.deleteFile(support.getLien());
                    }

                    supportRepository.delete(support);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Génère l'URL de téléchargement pour un support
     */
    public String getDownloadUrl(Integer id) {
        return supportRepository.findById(id)
                .map(support -> {
                    if (support.getLien() != null) {
                        return fileStorageService.getFileUrl(support.getLien());
                    }
                    return null;
                })
                .orElse(null);
    }
}