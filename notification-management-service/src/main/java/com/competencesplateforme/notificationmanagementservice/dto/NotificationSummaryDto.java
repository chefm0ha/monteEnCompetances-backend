package com.competencesplateforme.notificationmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class NotificationSummaryDto {

    private Long id;
    private String titre;
    private String contenu;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime seenAt;

    private boolean seen;

    // Constructors
    public NotificationSummaryDto() {}

    public NotificationSummaryDto(Long id, String titre, String contenu, LocalDateTime createdAt, LocalDateTime seenAt) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.createdAt = createdAt;
        this.seenAt = seenAt;
        this.seen = seenAt != null;
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

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) {
        this.seenAt = seenAt;
        this.seen = seenAt != null;
    }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}
