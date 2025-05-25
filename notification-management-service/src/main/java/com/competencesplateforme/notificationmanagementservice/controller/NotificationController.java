package com.competencesplateforme.notificationmanagementservice.controller;

import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationSummaryDto;
import com.competencesplateforme.notificationmanagementservice.service.AdminNotificationService;
import com.competencesplateforme.notificationmanagementservice.service.UserNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final UserNotificationService userNotificationService;
    private final AdminNotificationService adminNotificationService;

    public NotificationController(UserNotificationService userNotificationService,
                                  AdminNotificationService adminNotificationService) {
        this.userNotificationService = userNotificationService;
        this.adminNotificationService = adminNotificationService;
    }

    // ================================
    // ENDPOINTS POUR UTILISATEURS
    // ================================

    /**
     * GET /api/notifications/user/{userId}/latest
     * Récupère les 10 dernières notifications non vues, sinon les plus récentes
     */
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<List<NotificationSummaryDto>> getLatestUserNotifications(@PathVariable UUID userId) {
        List<NotificationSummaryDto> notifications = userNotificationService.getLatestNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/user/{userId}/{notificationId}
     * Récupère une notification par ID et la marque comme vue
     */
    @GetMapping("/user/{userId}/{notificationId}")
    public ResponseEntity<NotificationDto> getUserNotificationById(@PathVariable UUID userId,
                                                                   @PathVariable Long notificationId) {
        NotificationDto notification = userNotificationService.getNotificationById(userId, notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * PUT /api/notifications/user/{userId}/mark-seen
     * Marque plusieurs notifications comme vues
     */
    @PutMapping("/user/{userId}/mark-seen")
    public ResponseEntity<String> markUserNotificationsAsSeen(@PathVariable UUID userId,
                                                              @RequestBody List<Long> notificationIds) {
        userNotificationService.markNotificationsAsSeen(userId, notificationIds);
        return ResponseEntity.ok("Notifications marquées comme vues");
    }

    /**
     * GET /api/notifications/user/{userId}/all
     * Récupère toutes les notifications de l'utilisateur avec pagination
     */
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<Page<NotificationSummaryDto>> getAllUserNotifications(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationSummaryDto> notifications = userNotificationService.getAllUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/user/{userId}/unseen-count
     * Compte le nombre de notifications non vues
     */
    @GetMapping("/user/{userId}/unseen-count")
    public ResponseEntity<Long> getUnseenUserNotificationsCount(@PathVariable UUID userId) {
        Long count = userNotificationService.getUnseenCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * PUT /api/notifications/user/{userId}/mark-all-seen
     * Marque toutes les notifications comme vues
     */
    @PutMapping("/user/{userId}/mark-all-seen")
    public ResponseEntity<String> markAllUserNotificationsAsSeen(@PathVariable UUID userId) {
        userNotificationService.markAllAsSeen(userId);
        return ResponseEntity.ok("Toutes les notifications marquées comme vues");
    }

    // ================================
    // ENDPOINTS POUR ADMINS
    // ================================

    /**
     * GET /api/notifications/admin/{adminId}/latest
     * Récupère les 10 dernières notifications non vues pour l'admin, sinon les plus récentes
     */
    //TESTED
    @GetMapping("/admin/{adminId}/latest")
    public ResponseEntity<List<NotificationSummaryDto>> getLatestAdminNotifications(@PathVariable UUID adminId) {
        List<NotificationSummaryDto> notifications = adminNotificationService.getLatestNotifications(adminId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/admin/{adminId}/{notificationId}
     * Récupère une notification par ID et la marque comme vue pour l'admin
     */
    //TESTED
    @GetMapping("/admin/{adminId}/{notificationId}")
    public ResponseEntity<NotificationDto> getAdminNotificationById(@PathVariable UUID adminId,
                                                                    @PathVariable Long notificationId) {
        NotificationDto notification = adminNotificationService.getNotificationById(adminId, notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * PUT /api/notifications/admin/{adminId}/mark-seen
     * Marque plusieurs notifications comme vues pour l'admin
     */
    //TESTED
    @PutMapping("/admin/{adminId}/mark-seen")
    public ResponseEntity<String> markAdminNotificationsAsSeen(@PathVariable UUID adminId,
                                                               @RequestBody List<Long> notificationIds) {
        adminNotificationService.markNotificationsAsSeen(adminId, notificationIds);
        return ResponseEntity.ok("Notifications marquées comme vues");
    }

    /**
     * GET /api/notifications/admin/{adminId}/all
     * Récupère toutes les notifications du système avec pagination (pour admin)
     */
    @GetMapping("/admin/{adminId}/all")
    public ResponseEntity<Page<NotificationSummaryDto>> getAllAdminNotifications(
            @PathVariable UUID adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationSummaryDto> notifications = adminNotificationService.getAllUserNotifications(adminId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/admin/{adminId}/unseen-count
     * Compte le nombre de notifications non vues par l'admin
     */
    @GetMapping("/admin/{adminId}/unseen-count")
    public ResponseEntity<Long> getUnseenAdminNotificationsCount(@PathVariable UUID adminId) {
        Long count = adminNotificationService.getUnseenCount(adminId);
        return ResponseEntity.ok(count);
    }

    /**
     * PUT /api/notifications/admin/{adminId}/mark-all-seen
     * Marque toutes les notifications du système comme vues pour l'admin
     */
    @PutMapping("/admin/{adminId}/mark-all-seen")
    public ResponseEntity<String> markAllAdminNotificationsAsSeen(@PathVariable UUID adminId) {
        adminNotificationService.markAllAsSeen(adminId);
        return ResponseEntity.ok("Toutes les notifications marquées comme vues");
    }

    // ================================
    // ENDPOINT UNIFIÉ (OPTIONNEL)
    // ================================

    /**
     * GET /api/notifications/{userId}/latest?isAdmin=true/false
     * Endpoint unifié qui détecte automatiquement selon le paramètre isAdmin
     */
    @GetMapping("/{userId}/latest")
    public ResponseEntity<List<NotificationSummaryDto>> getLatestNotifications(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {

        if (isAdmin) {
            List<NotificationSummaryDto> notifications = adminNotificationService.getLatestNotifications(userId);
            return ResponseEntity.ok(notifications);
        } else {
            List<NotificationSummaryDto> notifications = userNotificationService.getLatestNotifications(userId);
            return ResponseEntity.ok(notifications);
        }
    }

    /**
     * GET /api/notifications/{userId}/{notificationId}?isAdmin=true/false
     * Endpoint unifié pour récupérer une notification
     */
    @GetMapping("/{userId}/{notificationId}")
    public ResponseEntity<NotificationDto> getNotificationById(
            @PathVariable UUID userId,
            @PathVariable Long notificationId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {

        if (isAdmin) {
            NotificationDto notification = adminNotificationService.getNotificationById(userId, notificationId);
            return ResponseEntity.ok(notification);
        } else {
            NotificationDto notification = userNotificationService.getNotificationById(userId, notificationId);
            return ResponseEntity.ok(notification);
        }
    }

    /**
     * PUT /api/notifications/{userId}/mark-seen?isAdmin=true/false
     * Endpoint unifié pour marquer des notifications comme vues
     */
    @PutMapping("/{userId}/mark-seen")
    public ResponseEntity<String> markNotificationsAsSeen(
            @PathVariable UUID userId,
            @RequestBody List<Long> notificationIds,
            @RequestParam(defaultValue = "false") boolean isAdmin) {

        if (isAdmin) {
            adminNotificationService.markNotificationsAsSeen(userId, notificationIds);
        } else {
            userNotificationService.markNotificationsAsSeen(userId, notificationIds);
        }

        return ResponseEntity.ok("Notifications marquées comme vues");
    }
}
