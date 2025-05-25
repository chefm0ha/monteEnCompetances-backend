package com.competencesplateforme.notificationmanagementservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNotification> userNotifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Notification() {}

    public Notification(String titre, String contenu) {
        this.titre = titre;
        this.contenu = contenu;
    }

    // Helper methods
    public void addUserNotification(UserNotification userNotification) {
        userNotifications.add(userNotification);
        userNotification.setNotification(this);
    }

    public void removeUserNotification(UserNotification userNotification) {
        userNotifications.remove(userNotification);
        userNotification.setNotification(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<UserNotification> getUserNotifications() { return userNotifications; }
    public void setUserNotifications(List<UserNotification> userNotifications) { this.userNotifications = userNotifications; }
}
