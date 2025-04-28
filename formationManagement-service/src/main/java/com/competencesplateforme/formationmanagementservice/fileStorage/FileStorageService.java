package com.competencesplateforme.formationmanagementservice.fileStorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String fileName, String folderName) throws IOException;

    String uploadFile(byte[] fileBytes, String fileName, String folderName);

    boolean deleteFile(String publicId);

    String getFileUrl(String url);
}