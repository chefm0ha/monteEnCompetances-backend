package com.competencesplateforme.notificationmanagementservice.service;

import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationSummaryDto;
import com.competencesplateforme.notificationmanagementservice.exception.NotificationNotFoundException;
import com.competencesplateforme.notificationmanagementservice.mapper.NotificationMapper;
import com.competencesplateforme.notificationmanagementservice.model.UserNotification;
import com.competencesplateforme.notificationmanagementservice.repository.UserNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationMapper notificationMapper;

    public UserNotificationService(UserNotificationRepository userNotificationRepository,
                                   NotificationMapper notificationMapper) {
        this.userNotificationRepository = userNotificationRepository;
        this.notificationMapper = notificationMapper;
    }

    // Récupère les 10 dernières notifications non vues, sinon les plus récentes
    public List<NotificationSummaryDto> getLatestNotifications(UUID userId) {
        // D'abord chercher les non vues (limit 10)
        List<UserNotification> unseenNotifications = userNotificationRepository.findUnseenByUserId(userId);

        if (unseenNotifications.size() >= 10) {
            // Prendre les 10 premières non vues
            return unseenNotifications.subList(0, 10).stream()
                    .map(this::convertToSummaryDto)
                    .toList();
        }

        // Si pas assez de non vues, prendre les 10 plus récentes
        Page<NotificationSummaryDto> recentNotifications = userNotificationRepository
                .findNotificationSummaryByUserId(userId, PageRequest.of(0, 10));

        return recentNotifications.getContent();
    }

    // Récupère une notification par ID et la marque comme vue
    public NotificationDto getNotificationById(UUID userId, Long notificationId) {
        UserNotification userNotification = userNotificationRepository
                .findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification non trouvée"));

        // Marquer comme vue
        if (!userNotification.isSeen()) {
            userNotification.markAsSeen();
            userNotificationRepository.save(userNotification);
        }

        return notificationMapper.toDto(userNotification.getNotification());
    }

    // Marque plusieurs notifications comme vues
    public void markNotificationsAsSeen(UUID userId, List<Long> notificationIds) {
        LocalDateTime now = LocalDateTime.now();
        for (Long notificationId : notificationIds) {
            userNotificationRepository.markAsSeen(userId, notificationId, now);
        }
    }

    // Retourne toutes les notifications par ordre de création
    public Page<NotificationSummaryDto> getAllUserNotifications(UUID userId, Pageable pageable) {
        return userNotificationRepository.findNotificationSummaryByUserId(userId, pageable);
    }

    // Compte les notifications non vues
    public Long getUnseenCount(UUID userId) {
        return userNotificationRepository.countUnseenByUserId(userId);
    }

    // Marque toutes les notifications comme vues
    public void markAllAsSeen(UUID userId) {
        userNotificationRepository.markAllAsSeen(userId, LocalDateTime.now());
    }

    private NotificationSummaryDto convertToSummaryDto(UserNotification userNotification) {
        return new NotificationSummaryDto(
                userNotification.getNotification().getId(),
                userNotification.getNotification().getTitre(),
                userNotification.getNotification().getContenu(),
                userNotification.getNotification().getCreatedAt(),
                userNotification.getSeenAt()
        );
    }
}
