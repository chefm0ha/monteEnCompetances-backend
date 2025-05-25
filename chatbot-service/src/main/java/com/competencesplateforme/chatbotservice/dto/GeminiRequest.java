package com.competencesplateforme.chatbotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {

    private List<Content> contents;
    private GenerationConfig generationConfig;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;

        public static Content text(String text) {
            return Content.builder()
                    .parts(List.of(Part.builder().text(text).build()))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationConfig {
        private Double temperature;
        private Integer maxOutputTokens;
        private Integer topK;
        private Double topP;
    }

    public static GeminiRequest create(String prompt, Double temperature, Integer maxTokens) {
        return GeminiRequest.builder()
                .contents(List.of(Content.text(prompt)))
                .generationConfig(GenerationConfig.builder()
                        .temperature(temperature)
                        .maxOutputTokens(maxTokens)
                        .topK(40)
                        .topP(0.95)
                        .build())
                .build();
    }
}
