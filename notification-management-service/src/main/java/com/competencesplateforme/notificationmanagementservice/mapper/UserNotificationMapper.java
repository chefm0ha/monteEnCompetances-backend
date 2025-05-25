package com.competencesplateforme.notificationmanagementservice.mapper;

import com.competencesplateforme.notificationmanagementservice.dto.UserNotificationDto;
import com.competencesplateforme.notificationmanagementservice.model.UserNotification;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserNotificationMapper {

    public UserNotificationDto toDto(UserNotification userNotification) {
        if (userNotification == null) return null;

        UserNotificationDto dto = new UserNotificationDto();
        dto.setUserId(userNotification.getUserId());
        dto.setNotificationId(userNotification.getNotificationId());
        dto.setSeenAt(userNotification.getSeenAt());
        dto.setSeen(userNotification.isSeen());

        return dto;
    }

    public UserNotificationDto toDtoWithNotification(UserNotification userNotification) {
        if (userNotification == null) return null;

        UserNotificationDto dto = toDto(userNotification);

        if (userNotification.getNotification() != null) {
            // Create lightweight notification DTO to avoid circular reference
            NotificationMapper notificationMapper = new NotificationMapper(this);
            dto.setNotification(notificationMapper.toLightDto(userNotification.getNotification()));
        }

        return dto;
    }

    public List<UserNotificationDto> toDtoList(List<UserNotification> userNotifications) {
        if (userNotifications == null) return Collections.emptyList();

        return userNotifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserNotificationDto> toDtoListWithNotifications(List<UserNotification> userNotifications) {
        if (userNotifications == null) return Collections.emptyList();

        return userNotifications.stream()
                .map(this::toDtoWithNotification)
                .collect(Collectors.toList());
    }

    public UserNotification toEntity(UserNotificationDto dto) {
        if (dto == null) return null;

        UserNotification userNotification = new UserNotification();
        userNotification.setUserId(dto.getUserId());
        userNotification.setNotificationId(dto.getNotificationId());
        userNotification.setSeenAt(dto.getSeenAt());

        return userNotification;
    }
}
