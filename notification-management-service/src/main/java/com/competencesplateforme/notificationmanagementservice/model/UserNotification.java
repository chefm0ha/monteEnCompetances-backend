package com.competencesplateforme.notificationmanagementservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users_notifications")
@IdClass(UserNotificationId.class)  // AJOUTER CETTE LIGNE
public class UserNotification {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", insertable = false, updatable = false)
    private Notification notification;

    @Column(name = "seen_at")
    private LocalDateTime seenAt;

    // Constructeurs
    public UserNotification() {}

    public UserNotification(UUID userId, Notification notification) {
        this.userId = userId;
        this.notification = notification;
        this.notificationId = notification.getId();
    }

    // Helper methods
    public void markAsSeen() {
        this.seenAt = LocalDateTime.now();
    }

    public boolean isSeen() {
        return seenAt != null;
    }

    // Getters et Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) {
        this.notification = notification;
        this.notificationId = notification != null ? notification.getId() : null;
    }

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) { this.seenAt = seenAt; }

    // equals et hashCode basés sur la clé composite
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNotification)) return false;
        UserNotification that = (UserNotification) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, notificationId);
    }
}
