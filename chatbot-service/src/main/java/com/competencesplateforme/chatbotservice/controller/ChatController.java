package com.competencesplateforme.chatbotservice.controller;

import com.competencesplateforme.chatbotservice.dto.ChatRequest;
import com.competencesplateforme.chatbotservice.dto.ChatResponse;
import com.competencesplateforme.chatbotservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(@Valid @RequestBody ChatRequest request) {
        log.debug("Nouvelle question reçue: {}", request.getQuestion());

        try {
            ChatResponse response = chatService.processQuestion(request);

            if (response.isError()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors du traitement de la question: {}", e.getMessage(), e);

            ChatResponse errorResponse = ChatResponse.error(
                    "Une erreur technique est survenue. Veuillez réessayer.",
                    request.getSessionId()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/conversation/{sessionId}")
    public ResponseEntity<Map<String, String>> clearConversation(@PathVariable String sessionId) {
        try {
            chatService.clearConversation(sessionId);

            Map<String, String> response = Map.of(
                    "message", "Conversation effacée avec succès",
                    "sessionId", sessionId
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'effacement de la conversation: {}", e.getMessage(), e);

            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur lors de l'effacement de la conversation",
                    "sessionId", sessionId
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        boolean isHealthy = chatService.isHealthy();

        Map<String, Object> health = Map.of(
                "status", isHealthy ? "UP" : "DOWN",
                "service", "chatbot-service",
                "timestamp", System.currentTimeMillis(),
                "features", "conversation-memory-enabled"
        );

        HttpStatus status = isHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getServiceInfo() {
        Map<String, String> info = Map.of(
                "service", "Chatbot Service",
                "version", "1.0.0",
                "description", "Assistant pédagogique pour les formations",
                "capabilities", "Questions sur formations, modules, supports et sujets pédagogiques",
                "memory", "Mémoire conversationnelle activée (6 derniers messages)"
        );

        return ResponseEntity.ok(info);
    }
}