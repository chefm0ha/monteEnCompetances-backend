package com.competencesplateforme.chatbotservice.service;

import com.competencesplateforme.chatbotservice.dto.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ConversationHistoryService {

    private final Cache conversationCache;
    private static final int MAX_HISTORY_SIZE = 6; // 3 échanges (user + assistant)

    public ConversationHistoryService(CacheManager cacheManager) {
        this.conversationCache = cacheManager.getCache("conversationHistory");
    }

    public void addUserMessage(String sessionId, String message) {
        ChatMessage userMessage = ChatMessage.userMessage(message);
        addMessageToHistory(sessionId, userMessage);
        log.debug("Message utilisateur ajouté pour la session: {}", sessionId);
    }

    public void addAssistantMessage(String sessionId, String message) {
        ChatMessage assistantMessage = ChatMessage.assistantMessage(message);
        addMessageToHistory(sessionId, assistantMessage);
        log.debug("Message assistant ajouté pour la session: {}", sessionId);
    }

    public List<ChatMessage> getConversationHistory(String sessionId) {
        Cache.ValueWrapper wrapper = conversationCache.get(sessionId);
        if (wrapper != null && wrapper.get() != null) {
            @SuppressWarnings("unchecked")
            List<ChatMessage> history = (List<ChatMessage>) wrapper.get();
            return new ArrayList<>(history);
        }
        return new ArrayList<>();
    }

    public String buildConversationContext(String sessionId) {
        List<ChatMessage> history = getConversationHistory(sessionId);

        if (history.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("\n\nCONTEXTE DE LA CONVERSATION PRÉCÉDENTE :\n");

        for (ChatMessage message : history) {
            if (message.isUser()) {
                context.append("Utilisateur: ").append(message.getContent()).append("\n");
            } else {
                context.append("Assistant: ").append(message.getContent()).append("\n");
            }
        }
        context.append("\n");

        log.debug("Contexte conversationnel construit pour session {}: {} messages",
                sessionId, history.size());

        return context.toString();
    }

    public void clearConversation(String sessionId) {
        conversationCache.evict(sessionId);
        log.debug("Conversation effacée pour la session: {}", sessionId);
    }

    private void addMessageToHistory(String sessionId, ChatMessage message) {
        List<ChatMessage> history = getConversationHistory(sessionId);

        // Ajouter le nouveau message
        history.add(message);

        // Limiter la taille de l'historique (garder les plus récents)
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(history.size() - MAX_HISTORY_SIZE, history.size());
        }

        // Sauvegarder dans le cache
        conversationCache.put(sessionId, history);
    }
}