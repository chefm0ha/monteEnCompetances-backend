package com.competencesplateforme.collaborateurmanagementservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final RestTemplate restTemplate;

    @Value("${notification-service.url:http://localhost:4005}")
    private String notificationServiceUrl;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Crée une notification pour un utilisateur
     */
    public void createNotification(String titre, String contenu, UUID userId) {
        try {
            String url = notificationServiceUrl + "/api/notifications/create/quick"
                    + "?titre=" + titre
                    + "&contenu=" + contenu
                    + "&userId=" + userId;

            restTemplate.postForEntity(url, null, String.class);

            logger.info("Notification envoyée à l'utilisateur: {}", userId);

        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de notification: {}", e.getMessage());
        }
    }

    /**
     * Crée une notification pour plusieurs utilisateurs
     */
    public void createNotification(String titre, String contenu, List<UUID> userIds) {
        try {
            // Appel JSON pour plusieurs utilisateurs
            String url = notificationServiceUrl + "/api/notifications/create";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = String.format(
                    "{\"titre\":\"%s\",\"contenu\":\"%s\",\"userIds\":[%s]}",
                    titre,
                    contenu,
                    userIds.stream().map(id -> "\"" + id + "\"").reduce((a, b) -> a + "," + b).orElse("")
            );

            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(url, entity, String.class);

            logger.info("Notification envoyée à {} utilisateurs", userIds.size());

        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de notifications multiples: {}", e.getMessage());
        }
    }
}
