package com.competencesplateforme.notificationmanagementservice.service;


import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationSummaryDto;
import com.competencesplateforme.notificationmanagementservice.exception.NotificationNotFoundException;
import com.competencesplateforme.notificationmanagementservice.mapper.NotificationMapper;
import com.competencesplateforme.notificationmanagementservice.model.Notification;
import com.competencesplateforme.notificationmanagementservice.model.UserNotification;
import com.competencesplateforme.notificationmanagementservice.repository.NotificationRepository;
import com.competencesplateforme.notificationmanagementservice.repository.UserNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationMapper notificationMapper;

    public AdminNotificationService(NotificationRepository notificationRepository,
                                    UserNotificationRepository userNotificationRepository,
                                    NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.notificationMapper = notificationMapper;
    }

    // Admin voit TOUTES les notifications - 10 dernières non vues, sinon les plus récentes
    public List<NotificationSummaryDto> getLatestNotifications(UUID adminId) {
        // Récupérer toutes les notifications du système (les 20 plus récentes pour filtrer)
        Page<Notification> allNotifications = notificationRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 20));

        // Convertir avec le statut "vu" de l'admin
        List<NotificationSummaryDto> summaries = allNotifications.getContent().stream()
                .map(notification -> {
                    Optional<UserNotification> adminNotif = userNotificationRepository
                            .findByUserIdAndNotificationId(adminId, notification.getId());

                    LocalDateTime seenAt = adminNotif.map(UserNotification::getSeenAt).orElse(null);

                    return new NotificationSummaryDto(
                            notification.getId(),
                            notification.getTitre(),
                            notification.getContenu(),
                            notification.getCreatedAt(),
                            seenAt
                    );
                })
                .toList();

        // Filtrer les non vues
        List<NotificationSummaryDto> unseen = summaries.stream()
                .filter(n -> !n.isSeen())
                .limit(10)
                .toList();

        // Si assez de non vues, les retourner
        if (unseen.size() >= 10) {
            return unseen;
        }

        // Sinon retourner les 10 plus récentes
        return summaries.stream().limit(10).toList();
    }

    // Admin peut voir n'importe quelle notification et la marque comme vue
    public NotificationDto getNotificationById(UUID adminId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification non trouvée"));

        // Créer ou mettre à jour l'entrée pour l'admin
        Optional<UserNotification> adminNotif = userNotificationRepository
                .findByUserIdAndNotificationId(adminId, notificationId);

        if (adminNotif.isPresent()) {
            if (!adminNotif.get().isSeen()) {
                adminNotif.get().markAsSeen();
                userNotificationRepository.save(adminNotif.get());
            }
        } else {
            // Créer une nouvelle entrée pour l'admin
            UserNotification newAdminNotif = new UserNotification(adminId, notification);
            newAdminNotif.markAsSeen();
            userNotificationRepository.save(newAdminNotif);
        }

        return notificationMapper.toDto(notification);
    }

    // Marquer plusieurs notifications comme vues pour l'admin
    public void markNotificationsAsSeen(UUID adminId, List<Long> notificationIds) {
        LocalDateTime now = LocalDateTime.now();

        for (Long notificationId : notificationIds) {
            Optional<UserNotification> adminNotif = userNotificationRepository
                    .findByUserIdAndNotificationId(adminId, notificationId);

            if (adminNotif.isPresent()) {
                if (!adminNotif.get().isSeen()) {
                    userNotificationRepository.markAsSeen(adminId, notificationId, now);
                }
            } else {
                // Créer entrée pour l'admin
                Notification notification = notificationRepository.findById(notificationId).orElse(null);
                if (notification != null) {
                    UserNotification newAdminNotif = new UserNotification(adminId, notification);
                    newAdminNotif.setSeenAt(now);
                    userNotificationRepository.save(newAdminNotif);
                }
            }
        }
    }

    // Admin voit TOUTES les notifications du système par ordre de création
    public Page<NotificationSummaryDto> getAllUserNotifications(UUID adminId, Pageable pageable) {
        Page<Notification> allNotifications = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<NotificationSummaryDto> summaries = allNotifications.getContent().stream()
                .map(notification -> {
                    Optional<UserNotification> adminNotif = userNotificationRepository
                            .findByUserIdAndNotificationId(adminId, notification.getId());

                    LocalDateTime seenAt = adminNotif.map(UserNotification::getSeenAt).orElse(null);

                    return new NotificationSummaryDto(
                            notification.getId(),
                            notification.getTitre(),
                            notification.getContenu(),
                            notification.getCreatedAt(),
                            seenAt
                    );
                })
                .toList();

        return new PageImpl<>(summaries, pageable, allNotifications.getTotalElements());
    }

    // Compte les notifications non vues par l'admin
    public Long getUnseenCount(UUID adminId) {
        long totalNotifications = notificationRepository.count();

        // Compter celles vues par l'admin
        long actuallySeenByAdmin = userNotificationRepository.countByUserIdAndSeenAtIsNotNull(adminId);

        return totalNotifications - actuallySeenByAdmin;
    }

    // Marque TOUTES les notifications du système comme vues pour l'admin
    public void markAllAsSeen(UUID adminId) {
        List<Notification> allNotifications = notificationRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : allNotifications) {
            Optional<UserNotification> adminNotif = userNotificationRepository
                    .findByUserIdAndNotificationId(adminId, notification.getId());

            if (adminNotif.isPresent()) {
                if (!adminNotif.get().isSeen()) {
                    userNotificationRepository.markAsSeen(adminId, notification.getId(), now);
                }
            } else {
                // Créer entrée pour l'admin
                UserNotification newAdminNotif = new UserNotification(adminId, notification);
                newAdminNotif.setSeenAt(now);
                userNotificationRepository.save(newAdminNotif);
            }
        }
    }
}
