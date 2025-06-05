package com.competencesplateforme.formationmanagementservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SupportProgressDTO {
    private UUID collaborateurId;
    private Integer supportId;
    private String supportTitle;
    private String supportType;
    private Boolean seen;
    private LocalDateTime seenAt;

    // Constructors
    public SupportProgressDTO() {}

    public SupportProgressDTO(UUID collaborateurId, Integer supportId, String supportTitle,
                              String supportType, Boolean seen) {
        this.collaborateurId = collaborateurId;
        this.supportId = supportId;
        this.supportTitle = supportTitle;
        this.supportType = supportType;
        this.seen = seen;
    }

    // Getters and Setters
    public UUID getCollaborateurId() { return collaborateurId; }
    public void setCollaborateurId(UUID collaborateurId) { this.collaborateurId = collaborateurId; }

    public Integer getSupportId() { return supportId; }
    public void setSupportId(Integer supportId) { this.supportId = supportId; }

    public String getSupportTitle() { return supportTitle; }
    public void setSupportTitle(String supportTitle) { this.supportTitle = supportTitle; }

    public String getSupportType() { return supportType; }
    public void setSupportType(String supportType) { this.supportType = supportType; }

    public Boolean getSeen() { return seen; }
    public void setSeen(Boolean seen) { this.seen = seen; }

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) { this.seenAt = seenAt; }
}