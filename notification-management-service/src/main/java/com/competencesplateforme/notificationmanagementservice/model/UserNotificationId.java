package com.competencesplateforme.notificationmanagementservice.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserNotificationId implements Serializable {

    private UUID userId;
    private Long notificationId;

    // Constructeurs
    public UserNotificationId() {}

    public UserNotificationId(UUID userId, Long notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }

    // Getters et Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    // equals et hashCode OBLIGATOIRES
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotificationId that = (UserNotificationId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, notificationId);
    }
}
