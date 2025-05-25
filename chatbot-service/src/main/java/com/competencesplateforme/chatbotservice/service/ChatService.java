package com.competencesplateforme.chatbotservice.service;

import com.competencesplateforme.chatbotservice.dto.ChatRequest;
import com.competencesplateforme.chatbotservice.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class ChatService {

    private final FormationDataService formationDataService;
    private final GeminiService geminiService;
    private final ConversationHistoryService conversationHistoryService;
    private final String systemPromptTemplate;
    private final String defaultResponse;

    public ChatService(FormationDataService formationDataService,
                       GeminiService geminiService,
                       ConversationHistoryService conversationHistoryService,
                       @Value("${chatbot.system-prompt-file}") Resource systemPromptFile,
                       @Value("${chatbot.default-response}") String defaultResponse) {
        this.formationDataService = formationDataService;
        this.geminiService = geminiService;
        this.conversationHistoryService = conversationHistoryService;
        this.defaultResponse = defaultResponse;
        this.systemPromptTemplate = loadSystemPrompt(systemPromptFile);
    }

    public ChatResponse processQuestion(ChatRequest request) {
        String sessionId = request.getSessionId() != null ?
                request.getSessionId() :
                UUID.randomUUID().toString();

        try {
            log.debug("Traitement de la question pour session {}: {}", sessionId, request.getQuestion());

            // Étape 1: Sauvegarder la question de l'utilisateur dans l'historique
            conversationHistoryService.addUserMessage(sessionId, request.getQuestion());

            // Étape 2: Récupérer le contexte des formations (Mode 1)
            String formationContext = formationDataService.getFormationContextAsString();

            // Étape 3: Récupérer l'historique de la conversation
            String conversationContext = conversationHistoryService.buildConversationContext(sessionId);

            // Étape 4: Construire le prompt enrichi (Mode 2)
            String enrichedPrompt = buildEnrichedPrompt(formationContext, conversationContext, request.getQuestion());

            // Étape 5: Appeler Gemini API
            String geminiResponse = geminiService.generateResponse(enrichedPrompt);

            // Étape 6: Sauvegarder la réponse dans l'historique
            conversationHistoryService.addAssistantMessage(sessionId, geminiResponse);

            // Étape 7: Construire la réponse finale
            log.debug("Réponse générée avec succès pour la session: {}", sessionId);
            return ChatResponse.success(geminiResponse, sessionId);

        } catch (Exception e) {
            log.error("Erreur lors du traitement de la question pour session {}: {}", sessionId, e.getMessage(), e);
            return ChatResponse.error(defaultResponse, sessionId);
        }
    }


    private String buildEnrichedPrompt(String formationContext, String conversationContext, String userQuestion) {
        // Construire le contexte d'historique
        String historySection = "";
        if (!conversationContext.isEmpty()) {
            historySection = "HISTORIQUE DE LA CONVERSATION :\n" + conversationContext;
        }

        // Construire le prompt complet
        return systemPromptTemplate
                .replace("{FORMATION_CONTEXT}", formationContext)
                .replace("{CONVERSATION_HISTORY}", historySection)
                .replace("{USER_QUESTION}", userQuestion);
    }

    private String loadSystemPrompt(Resource systemPromptFile) {
        try {
            return StreamUtils.copyToString(
                    systemPromptFile.getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            log.error("Impossible de charger le prompt système: {}", e.getMessage());
            return "Tu es un assistant pédagogique. Réponds aux questions sur les formations.\n\nFormations: {FORMATION_CONTEXT}\n\nQuestion: {USER_QUESTION}";
        }
    }

    public boolean isHealthy() {
        try {
            formationDataService.getFormationContext();
            return true;
        } catch (Exception e) {
            log.warn("Service chatbot non disponible: {}", e.getMessage());
            return false;
        }
    }

    // Méthode pour effacer une conversation
    public void clearConversation(String sessionId) {
        conversationHistoryService.clearConversation(sessionId);
        log.debug("Conversation effacée pour la session: {}", sessionId);
    }
}