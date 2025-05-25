package com.competencesplateforme.chatbotservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gemini.api")
public class GeminiConfig {

    private String key;
    private String baseUrl;
    private String model;
    private int timeout;
    private int maxTokens;
    private double temperature;

    public String getGenerateContentUrl() {
        return baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + key;
    }
}