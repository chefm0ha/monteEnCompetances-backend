package com.competencesplateforme.chatbotservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "La question ne peut pas être vide")
    @Size(max = 500, message = "La question ne peut pas dépasser 500 caractères")
    private String question;

    private String sessionId; // Optionnel pour tracking
}
