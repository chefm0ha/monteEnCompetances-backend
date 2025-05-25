package com.competencesplateforme.notificationmanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class CreateNotificationRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @NotEmpty(message = "La liste des utilisateurs ne peut pas être vide")
    private List<UUID> userIds;

    // Constructors
    public CreateNotificationRequest() {}

    public CreateNotificationRequest(String titre, String contenu, List<UUID> userIds) {
        this.titre = titre;
        this.contenu = contenu;
        this.userIds = userIds;
    }

    // Getters and Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public List<UUID> getUserIds() { return userIds; }
    public void setUserIds(List<UUID> userIds) { this.userIds = userIds; }
}
