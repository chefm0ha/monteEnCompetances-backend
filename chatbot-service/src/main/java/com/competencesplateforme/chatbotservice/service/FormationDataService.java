package com.competencesplateforme.chatbotservice.service;

import com.competencesplateforme.chatbotservice.client.FormationServiceClient;
import com.competencesplateforme.chatbotservice.dto.FormationContextDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FormationDataService {

    private final FormationServiceClient formationServiceClient;

    public FormationDataService(FormationServiceClient formationServiceClient) {
        this.formationServiceClient = formationServiceClient;
    }

    @Cacheable(value = "formationContext", unless = "#result.formations.isEmpty()")
    public FormationContextDTO getFormationContext() {
        log.debug("Récupération du contexte des formations");

        FormationContextDTO context = formationServiceClient.getAllFormationsContext();

        if (context.getFormations().isEmpty()) {
            log.warn("Aucune formation trouvée dans le service formation");
        } else {
            log.debug("Contexte récupéré avec {} formations", context.getFormations().size());
        }

        return context;
    }

    public String getFormationContextAsString() {
        FormationContextDTO context = getFormationContext();
        return context.toContextString();
    }

    // Méthode pour forcer le rafraîchissement du cache si nécessaire
    public FormationContextDTO refreshFormationContext() {
        log.debug("Rafraîchissement forcé du contexte des formations");
        return formationServiceClient.getAllFormationsContext();
    }
}
