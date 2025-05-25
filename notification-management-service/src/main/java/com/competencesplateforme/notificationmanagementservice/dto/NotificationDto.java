package com.competencesplateforme.notificationmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<UserNotificationDto> userNotifications;
    private Integer totalUsers;
    private Integer seenCount;

    // Constructors
    public NotificationDto() {}

    public NotificationDto(String titre, String contenu) {
        this.titre = titre;
        this.contenu = contenu;
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

    public List<UserNotificationDto> getUserNotifications() { return userNotifications; }
    public void setUserNotifications(List<UserNotificationDto> userNotifications) { this.userNotifications = userNotifications; }

    public Integer getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Integer totalUsers) { this.totalUsers = totalUsers; }

    public Integer getSeenCount() { return seenCount; }
    public void setSeenCount(Integer seenCount) { this.seenCount = seenCount; }
}
