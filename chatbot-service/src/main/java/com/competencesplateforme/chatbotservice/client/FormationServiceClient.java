package com.competencesplateforme.chatbotservice.client;

import com.competencesplateforme.chatbotservice.dto.FormationContextDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FormationServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String formationServiceBaseUrl;

    public FormationServiceClient(RestTemplate restTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${services.formation.base-url}") String formationServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.formationServiceBaseUrl = formationServiceBaseUrl;
    }

    public FormationContextDTO getAllFormationsContext() {
        try {
            log.debug("Récupération des formations depuis: {}", formationServiceBaseUrl);

            // Appel pour récupérer toutes les formations complètes avec modules et supports
            String url = formationServiceBaseUrl + "/api/admin/formations/complete";
            String response = restTemplate.getForObject(url, String.class);

            // Conversion JSON vers notre DTO
            List<Map<String, Object>> formationsData = objectMapper.readValue(
                    response, new TypeReference<List<Map<String, Object>>>() {}
            );

            List<FormationContextDTO.FormationSummary> formations = formationsData.stream()
                    .map(this::mapToFormationSummary)
                    .collect(Collectors.toList());

            log.debug("Formations récupérées: {}", formations.size());
            return FormationContextDTO.builder()
                    .formations(formations)
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des formations: {}", e.getMessage(), e);
            return FormationContextDTO.builder()
                    .formations(List.of())
                    .build();
        }
    }

    private FormationContextDTO.FormationSummary mapToFormationSummary(Map<String, Object> data) {
        return FormationContextDTO.FormationSummary.builder()
                .id((Integer) data.get("id"))
                .titre((String) data.get("titre"))
                .description((String) data.get("description"))
                .type((String) data.get("type"))
                .duree(data.get("duree") != null ? new BigDecimal(data.get("duree").toString()) : BigDecimal.ZERO)
                .modules(mapToModules((List<Map<String, Object>>) data.get("modules")))
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<FormationContextDTO.ModuleSummary> mapToModules(List<Map<String, Object>> modulesData) {
        if (modulesData == null) return List.of();

        return modulesData.stream()
                .map(moduleData -> FormationContextDTO.ModuleSummary.builder()
                        .id((Integer) moduleData.get("id"))
                        .titre((String) moduleData.get("titre"))
                        .description((String) moduleData.get("description"))
                        .supports(mapToSupports((List<Map<String, Object>>) moduleData.get("supports")))
                        .build())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<FormationContextDTO.SupportSummary> mapToSupports(List<Map<String, Object>> supportsData) {
        if (supportsData == null) return List.of();

        return supportsData.stream()
                .map(supportData -> FormationContextDTO.SupportSummary.builder()
                        .id((Integer) supportData.get("id"))
                        .titre((String) supportData.get("titre"))
                        .description((String) supportData.get("description"))
                        .type((String) supportData.get("type"))
                        .duree(supportData.get("duree") != null ?
                                new BigDecimal(supportData.get("duree").toString()) : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
    }
}