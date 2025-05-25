package com.competencesplateforme.notificationmanagementservice.controller;


import com.competencesplateforme.notificationmanagementservice.dto.CreateNotificationRequest;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.service.NotificationCreationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/create")

public class NotificationCreationController {

    private final NotificationCreationService notificationCreationService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationCreationController.class);


    public NotificationCreationController(NotificationCreationService notificationCreationService) {
        this.notificationCreationService = notificationCreationService;
    }

    /**
     * POST /api/notifications/create
     * Crée une notification pour une liste d'utilisateurs
     */
    // TESTED
    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        NotificationDto createdNotification = notificationCreationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * POST /api/notifications/create/single-user
     * Crée une notification pour un seul utilisateur
     */
    @PostMapping("/single-user")
    public ResponseEntity<NotificationDto> createNotificationForSingleUser(
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam UUID userId) {

        CreateNotificationRequest request = new CreateNotificationRequest(titre, contenu, List.of(userId));
        NotificationDto createdNotification = notificationCreationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * POST /api/notifications/create/multiple-users
     * Crée une notification pour plusieurs utilisateurs (IDs en paramètres)
     */
    @PostMapping("/multiple-users")
    public ResponseEntity<NotificationDto> createNotificationForMultipleUsers(
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam List<UUID> userIds) {

        CreateNotificationRequest request = new CreateNotificationRequest(titre, contenu, userIds);
        NotificationDto createdNotification = notificationCreationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * POST /api/notifications/create/broadcast
     * Crée une notification de diffusion (pour tous les utilisateurs du système)
     * Sera appelé avec une liste vide - le service se charge de récupérer tous les utilisateurs
     */
    @PostMapping("/broadcast")
    public ResponseEntity<NotificationDto> createBroadcastNotification(
            @RequestParam String titre,
            @RequestParam String contenu) {

        NotificationDto createdNotification = notificationCreationService.createBroadcastNotification(titre, contenu);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * POST /api/notifications/create/quick
     * Endpoint rapide pour les autres microservices (sans validation complexe)
     */
    // TESTED
    @PostMapping("/quick")
    public ResponseEntity<String> createQuickNotification(
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam UUID userId) {

        notificationCreationService.createQuickNotification(titre, contenu, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Notification créée avec succès");
    }

    /**
     * POST /api/notifications/create/batch
     * Crée plusieurs notifications en une seule fois
     */
    @PostMapping("/batch")
    public ResponseEntity<List<NotificationDto>> createBatchNotifications(
            @Valid @RequestBody List<CreateNotificationRequest> requests) {

        List<NotificationDto> createdNotifications = notificationCreationService.createBatchNotifications(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotifications);
    }

    /**
     * POST /api/notifications/create/admin-broadcast
     * Crée une notification spécifiquement pour l'administrateur système
     */
    @PostMapping("/admin-broadcast")
    public ResponseEntity<NotificationDto> createAdminBroadcastNotification(
            @RequestParam String titre,
            @RequestParam String contenu) {

        try {
            // ID fixe de l'administrateur système
            UUID adminId = UUID.fromString("223e4567-e89b-12d3-a456-426614174006");

            CreateNotificationRequest request = new CreateNotificationRequest(titre, contenu, List.of(adminId));
            NotificationDto createdNotification = notificationCreationService.createNotification(request);

            logger.info("Notification admin créée: '{}' pour l'administrateur: {}", titre, adminId);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);

        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
