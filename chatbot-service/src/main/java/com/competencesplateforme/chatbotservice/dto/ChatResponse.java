package com.competencesplateforme.chatbotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String response;
    private LocalDateTime timestamp;
    private String sessionId;
    private List<String> suggestedFormations; // Formations suggérées
    private boolean isError;
    private String errorMessage;

    public static ChatResponse success(String response, String sessionId) {
        return ChatResponse.builder()
                .response(response)
                .timestamp(LocalDateTime.now())
                .sessionId(sessionId)
                .isError(false)
                .build();
    }

    public static ChatResponse error(String errorMessage, String sessionId) {
        return ChatResponse.builder()
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .sessionId(sessionId)
                .isError(true)
                .build();
    }
}
