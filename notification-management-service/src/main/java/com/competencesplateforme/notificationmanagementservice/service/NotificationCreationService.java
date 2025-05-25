package com.competencesplateforme.notificationmanagementservice.service;

import com.competencesplateforme.notificationmanagementservice.dto.CreateNotificationRequest;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.mapper.NotificationMapper;
import com.competencesplateforme.notificationmanagementservice.model.Notification;
import com.competencesplateforme.notificationmanagementservice.model.UserNotification;
import com.competencesplateforme.notificationmanagementservice.repository.NotificationRepository;
import com.competencesplateforme.notificationmanagementservice.repository.UserNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationCreationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCreationService.class);

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationCreationService(NotificationRepository notificationRepository,
                                       UserNotificationRepository userNotificationRepository,
                                       NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Crée une notification pour une liste d'utilisateurs
     */
    public NotificationDto createNotification(CreateNotificationRequest request) {
        logger.info("Création d'une notification '{}' pour {} utilisateurs", request.getTitre(), request.getUserIds().size());

        // 1. Créer la notification
        Notification notification = notificationMapper.fromCreateRequest(request);
        Notification savedNotification = notificationRepository.save(notification);

        // 2. Créer les liens UserNotification pour chaque utilisateur
        List<UserNotification> userNotifications = new ArrayList<>();
        for (UUID userId : request.getUserIds()) {
            UserNotification userNotification = new UserNotification(userId, savedNotification);
            userNotifications.add(userNotification);
        }

        // 3. Sauvegarder tous les liens en batch
        userNotificationRepository.saveAll(userNotifications);

        logger.info("Notification créée avec succès ID: {} pour {} utilisateurs", savedNotification.getId(), userNotifications.size());

        return notificationMapper.toDto(savedNotification);
    }

    /**
     * Crée une notification de diffusion pour tous les utilisateurs
     * NOTE: Cette méthode nécessite un service pour récupérer tous les utilisateurs du système
     */
    public NotificationDto createBroadcastNotification(String titre, String contenu) {
        logger.info("Création d'une notification de diffusion: {}", titre);

        // 1. Créer la notification
        Notification notification = new Notification(titre, contenu);
        Notification savedNotification = notificationRepository.save(notification);

        // 2. TODO: Récupérer tous les utilisateurs du système
        // List<UUID> allUserIds = userService.getAllUserIds();
        // Pour l'instant, on crée sans utilisateurs - ils seront ajoutés par un autre processus

        logger.info("Notification de diffusion créée avec ID: {}", savedNotification.getId());

        return notificationMapper.toDto(savedNotification);
    }

    /**
     * Crée une notification rapide pour un utilisateur (sans retourner les détails)
     */
    public void createQuickNotification(String titre, String contenu, UUID userId) {
        logger.debug("Création rapide d'une notification pour l'utilisateur: {}", userId);

        // 1. Créer et sauvegarder la notification
        Notification notification = new Notification(titre, contenu);
        Notification savedNotification = notificationRepository.save(notification);

        // 2. Créer le lien UserNotification
        UserNotification userNotification = new UserNotification(userId, savedNotification);
        userNotificationRepository.save(userNotification);

        logger.debug("Notification rapide créée avec ID: {} pour utilisateur: {}", savedNotification.getId(), userId);
    }

    /**
     * Crée plusieurs notifications en batch
     */
    public List<NotificationDto> createBatchNotifications(List<CreateNotificationRequest> requests) {
        logger.info("Création de {} notifications en batch", requests.size());

        List<NotificationDto> createdNotifications = new ArrayList<>();

        for (CreateNotificationRequest request : requests) {
            try {
                NotificationDto created = createNotification(request);
                createdNotifications.add(created);
            } catch (Exception e) {
                logger.error("Erreur lors de la création de la notification: {}", request.getTitre(), e);
                // Continuer avec les autres notifications
            }
        }

        logger.info("Batch terminé: {} notifications créées sur {}", createdNotifications.size(), requests.size());

        return createdNotifications;
    }

    /**
     * Ajoute des utilisateurs à une notification existante
     */
    public void addUsersToNotification(Long notificationId, List<UUID> userIds) {
        logger.info("Ajout de {} utilisateurs à la notification {}", userIds.size(), notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée: " + notificationId));

        List<UserNotification> userNotifications = userIds.stream()
                .map(userId -> new UserNotification(userId, notification))
                .collect(Collectors.toList());

        userNotificationRepository.saveAll(userNotifications);

        logger.info("Utilisateurs ajoutés avec succès à la notification {}", notificationId);
    }
}
