package com.competencesplateforme.notificationmanagementservice.controller;


import com.competencesplateforme.notificationmanagementservice.dto.CreateNotificationRequest;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.service.NotificationCreationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/create")

public class NotificationCreationController {

    private final NotificationCreationService notificationCreationService;

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
}
