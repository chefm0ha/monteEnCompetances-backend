package com.competencesplateforme.chatbotservice.service;

import com.competencesplateforme.chatbotservice.config.GeminiConfig;
import com.competencesplateforme.chatbotservice.dto.GeminiRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class GeminiService {

    private final WebClient webClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    public GeminiService(WebClient geminiWebClient, GeminiConfig geminiConfig, ObjectMapper objectMapper) {
        this.webClient = geminiWebClient;
        this.geminiConfig = geminiConfig;
        this.objectMapper = objectMapper;
    }

    public String generateResponse(String prompt) {
        try {
            log.debug("Envoi de la requête à Gemini API");

            GeminiRequest request = GeminiRequest.create(
                    prompt,
                    geminiConfig.getTemperature(),
                    geminiConfig.getMaxTokens()
            );

            String response = webClient
                    .post()
                    .uri(geminiConfig.getGenerateContentUrl())
                    .header("Content-Type", "application/json")
                    .body(Mono.just(request), GeminiRequest.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(geminiConfig.getTimeout()))
                    .block();

            return extractTextFromResponse(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'appel à Gemini API: {}", e.getMessage(), e);
            return "Je rencontre actuellement des difficultés techniques. Pouvez-vous réessayer dans quelques instants ?";
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            log.warn("Format de réponse Gemini inattendu: {}", jsonResponse);
            return "Désolé, je n'ai pas pu traiter votre demande correctement.";

        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de la réponse Gemini: {}", e.getMessage());
            return "Erreur lors du traitement de la réponse.";
        }
    }
}
