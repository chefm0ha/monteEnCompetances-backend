package com.competencesplateforme.notificationmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNotificationDto {

    private UUID userId;
    private Long notificationId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime seenAt;

    private boolean seen;
    private NotificationDto notification;

    // Constructors
    public UserNotificationDto() {}

    public UserNotificationDto(UUID userId, Long notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
        this.seen = false;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) {
        this.seenAt = seenAt;
        this.seen = seenAt != null;
    }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public NotificationDto getNotification() { return notification; }
    public void setNotification(NotificationDto notification) { this.notification = notification; }
}
