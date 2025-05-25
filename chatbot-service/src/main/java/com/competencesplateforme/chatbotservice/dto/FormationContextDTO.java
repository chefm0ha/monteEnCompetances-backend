package com.competencesplateforme.chatbotservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationContextDTO {

    private List<FormationSummary> formations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormationSummary {
        private Integer id;
        private String titre;
        private String description;
        private String type;
        private BigDecimal duree;
        private List<ModuleSummary> modules;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleSummary {
        private Integer id;
        private String titre;
        private String description;
        private List<SupportSummary> supports;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportSummary {
        private Integer id;
        private String titre;
        private String description;
        private String type;
        private BigDecimal duree;
    }

    // Méthode utilitaire pour convertir en texte pour le prompt
    public String toContextString() {
        StringBuilder context = new StringBuilder();
        context.append("CATALOGUE DE FORMATIONS DISPONIBLES :\n\n");

        for (FormationSummary formation : formations) {
            context.append("📚 FORMATION: ").append(formation.getTitre()).append("\n");
            context.append("   Type: ").append(formation.getType()).append("\n");
            context.append("   Durée: ").append(formation.getDuree()).append("h\n");
            context.append("   Description: ").append(formation.getDescription()).append("\n");

            if (formation.getModules() != null && !formation.getModules().isEmpty()) {
                context.append("   📖 MODULES:\n");
                for (ModuleSummary module : formation.getModules()) {
                    context.append("      - ").append(module.getTitre());
                    if (module.getDescription() != null) {
                        context.append(": ").append(module.getDescription());
                    }
                    context.append("\n");

                    if (module.getSupports() != null && !module.getSupports().isEmpty()) {
                        context.append("        📄 Supports: ");
                        for (SupportSummary support : module.getSupports()) {
                            context.append(support.getTitre()).append(" (").append(support.getType()).append("), ");
                        }
                        context.append("\n");
                    }
                }
            }
            context.append("\n");
        }

        return context.toString();
    }
}
