package com.competencesplateforme.notificationmanagementservice.mapper;

import com.competencesplateforme.notificationmanagementservice.dto.CreateNotificationRequest;
import com.competencesplateforme.notificationmanagementservice.dto.NotificationDto;
import com.competencesplateforme.notificationmanagementservice.model.Notification;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    private final UserNotificationMapper userNotificationMapper;

    public NotificationMapper(UserNotificationMapper userNotificationMapper) {
        this.userNotificationMapper = userNotificationMapper;
    }

    public NotificationDto toDto(Notification notification) {
        if (notification == null) return null;

        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitre(notification.getTitre());
        dto.setContenu(notification.getContenu());
        dto.setCreatedAt(notification.getCreatedAt());

        if (notification.getUserNotifications() != null) {
            dto.setUserNotifications(
                    notification.getUserNotifications().stream()
                            .map(userNotificationMapper::toDto)
                            .collect(Collectors.toList())
            );
            dto.setTotalUsers(notification.getUserNotifications().size());
            dto.setSeenCount((int) notification.getUserNotifications().stream()
                    .mapToLong(un -> un.isSeen() ? 1 : 0)
                    .sum());
        }

        return dto;
    }

    public List<NotificationDto> toDtoList(List<Notification> notifications) {
        if (notifications == null) return Collections.emptyList();

        return notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Notification toEntity(NotificationDto dto) {
        if (dto == null) return null;

        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setTitre(dto.getTitre());
        notification.setContenu(dto.getContenu());
        notification.setCreatedAt(dto.getCreatedAt());

        return notification;
    }

    public Notification fromCreateRequest(CreateNotificationRequest request) {
        if (request == null) return null;

        return new Notification(request.getTitre(), request.getContenu());
    }

    // Lightweight DTO without user notifications (for performance)
    public NotificationDto toLightDto(Notification notification) {
        if (notification == null) return null;

        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitre(notification.getTitre());
        dto.setContenu(notification.getContenu());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }
}
