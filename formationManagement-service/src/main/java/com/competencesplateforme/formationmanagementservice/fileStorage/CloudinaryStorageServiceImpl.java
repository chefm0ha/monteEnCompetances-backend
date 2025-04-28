package com.competencesplateforme.formationmanagementservice.fileStorage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryStorageServiceImpl.class);

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryStorageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName, String folderName) throws IOException {
        try {
            return uploadFile(file.getBytes(), fileName, folderName);
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier: {}", e.getMessage());
            throw new FileStorageException("Impossible de lire le fichier", e);
        }
    }

    @Override
    public String uploadFile(byte[] fileBytes, String fileName, String folderName) {
        try {
            // Générer un nom unique pour éviter les conflits
            String uniqueFileName = fileName + "_" + UUID.randomUUID().toString().substring(0, 8);

            // Préparer les options pour l'upload
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("public_id", uniqueFileName);
            options.put("overwrite", true);
            options.put("resource_type", "auto"); // Détection automatique du type de ressource
            options.put("access_mode", "public");

            // Uploader le fichier sur Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(fileBytes, options);

            // Récupérer l'URL sécurisée directement depuis la réponse
            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            logger.info("Fichier téléchargé avec succès. Public ID: {}, URL: {}", publicId, secureUrl);

            // Retourner l'URL complète au lieu de l'identifiant public
            return secureUrl;
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement du fichier: {}", e.getMessage());
            throw new FileStorageException("Impossible de télécharger le fichier vers Cloudinary", e);
        }
    }

    @Override
    public boolean deleteFile(String publicId) {
        try {
            // Supprimer le fichier de Cloudinary
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            // Vérifier si la suppression a réussi
            String status = (String) result.get("result");
            boolean success = "ok".equals(status);

            if (success) {
                logger.info("Fichier supprimé avec succès. Public ID: {}", publicId);
            } else {
                logger.warn("Échec de la suppression du fichier. Public ID: {}, Statut: {}", publicId, status);
            }

            return success;
        } catch (IOException e) {
            logger.error("Erreur lors de la suppression du fichier: {}", e.getMessage());
            throw new FileStorageException("Impossible de supprimer le fichier de Cloudinary", e);
        }
    }

    @Override
    public String getFileUrl(String url) {
        // Comme nous stockons directement l'URL complète, nous la retournons simplement
        return url;
    }
}