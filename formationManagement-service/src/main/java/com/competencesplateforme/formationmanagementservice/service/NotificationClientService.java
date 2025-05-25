package com.competencesplateforme.formationmanagementservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationClientService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8080}")
    private String notificationServiceUrl;

    public NotificationClientService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Crée une notification pour une liste d'utilisateurs
     */
    public void createNotification(String titre, String contenu, List<UUID> userIds) {
        try {
            String url = notificationServiceUrl + "/api/notifications/create";

            CreateNotificationRequest request = new CreateNotificationRequest(titre, contenu, userIds);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateNotificationRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(url, entity, String.class);

        } catch (Exception e) {
            // Log l'erreur mais ne pas bloquer le processus principal
            System.err.println("Erreur lors de la création de notification: " + e.getMessage());
        }
    }

    /**
     * Crée une notification pour un seul utilisateur
     */
    public void createNotificationForUser(String titre, String contenu, UUID userId) {
        createNotification(titre, contenu, List.of(userId));
    }

    /**
     * Crée une notification pour tous les admins
     */
    public void createNotificationForAdmins(String titre, String contenu) {
        // Vous devrez implémenter la logique pour récupérer les IDs des admins
        // Pour l'instant, exemple avec des IDs fictifs
        List<UUID> adminIds = getAdminIds();
        createNotification(titre, contenu, adminIds);
    }

    private List<UUID> getAdminIds() {
        // TODO: Implémenter la récupération des IDs des admins
        // Retourner une liste vide pour l'instant
        return List.of();
    }

    // Classe interne pour la requête
    public static class CreateNotificationRequest {
        private String titre;
        private String contenu;
        private List<UUID> userIds;

        public CreateNotificationRequest(String titre, String contenu, List<UUID> userIds) {
            this.titre = titre;
            this.contenu = contenu;
            this.userIds = userIds;
        }

        // Getters et setters
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getContenu() { return contenu; }
        public void setContenu(String contenu) { this.contenu = contenu; }

        public List<UUID> getUserIds() { return userIds; }
        public void setUserIds(List<UUID> userIds) { this.userIds = userIds; }
    }
}
