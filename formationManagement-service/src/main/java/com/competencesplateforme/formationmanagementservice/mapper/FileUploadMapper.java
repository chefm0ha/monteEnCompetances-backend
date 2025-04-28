package com.competencesplateforme.formationmanagementservice.mapper;

import com.competencesplateforme.formationmanagementservice.dto.FileUploadDTO;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
public class FileUploadMapper {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileUploadMapper(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Crée un DTO à partir des informations de fichier et de l'ID public de Cloudinary
     */
    public FileUploadDTO toDTO(MultipartFile file, String publicId) {
        if (file == null || publicId == null) {
            return null;
        }

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileName(file.getOriginalFilename());
        dto.setPublicId(publicId);
        dto.setFileUrl(fileStorageService.getFileUrl(publicId));
        dto.setFileType(file.getContentType());
        dto.setFileSize(file.getSize());

        return dto;
    }

    /**
     * Crée un DTO à partir d'un résultat d'upload Cloudinary
     */
    public FileUploadDTO toDTO(Map<String, Object> uploadResult) {
        if (uploadResult == null) {
            return null;
        }

        FileUploadDTO dto = new FileUploadDTO();

        // Extraire les informations depuis le résultat d'upload
        if (uploadResult.containsKey("original_filename")) {
            dto.setFileName((String) uploadResult.get("original_filename"));
        }

        if (uploadResult.containsKey("public_id")) {
            dto.setPublicId((String) uploadResult.get("public_id"));
        }

        if (uploadResult.containsKey("secure_url")) {
            dto.setFileUrl((String) uploadResult.get("secure_url"));
        }

        if (uploadResult.containsKey("resource_type")) {
            dto.setFileType((String) uploadResult.get("resource_type"));
        }

        if (uploadResult.containsKey("bytes")) {
            Object bytes = uploadResult.get("bytes");
            if (bytes instanceof Number) {
                dto.setFileSize(((Number) bytes).longValue());
            }
        }

        return dto;
    }
}