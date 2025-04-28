package com.competencesplateforme.formationmanagementservice.controller;

import com.competencesplateforme.formationmanagementservice.dto.*;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import com.competencesplateforme.formationmanagementservice.mapper.*;
import com.competencesplateforme.formationmanagementservice.model.*;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/collaborateur/formations")
public class CollaborateurFormationController {

    private final FormationService formationService;
    private final ModuleService moduleService;
    private final SupportService supportService;
    private final QuizService quizService;
    private final CollaborateurFormationService collaborateurFormationService;
    private final FileStorageService fileStorageService;

    private final FormationMapper formationMapper;
    private final ModuleMapper moduleMapper;
    private final SupportMapper supportMapper;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final CollaborateurFormationMapper collaborateurFormationMapper;

    @Autowired
    public CollaborateurFormationController(
            FormationService formationService,
            ModuleService moduleService,
            SupportService supportService,
            QuizService quizService,
            CollaborateurFormationService collaborateurFormationService,
            FileStorageService fileStorageService,
            FormationMapper formationMapper,
            ModuleMapper moduleMapper,
            SupportMapper supportMapper,
            QuizMapper quizMapper,
            QuestionMapper questionMapper,
            CollaborateurFormationMapper collaborateurFormationMapper) {
        this.formationService = formationService;
        this.moduleService = moduleService;
        this.supportService = supportService;
        this.quizService = quizService;
        this.collaborateurFormationService = collaborateurFormationService;
        this.fileStorageService = fileStorageService;
        this.formationMapper = formationMapper;
        this.moduleMapper = moduleMapper;
        this.supportMapper = supportMapper;
        this.quizMapper = quizMapper;
        this.questionMapper = questionMapper;
        this.collaborateurFormationMapper = collaborateurFormationMapper;
    }

    // ======== CONSULTATION DES FORMATIONS ========

    @GetMapping //tested
    public ResponseEntity<List<FormationDTO>> getAllFormations() {
        List<Formation> formations = formationService.getAllFormations();
        return ResponseEntity.ok(formationMapper.toDTOList(formations));
    }

    @GetMapping("/{id}") //tested
    public ResponseEntity<FormationDTO> getFormationById(@PathVariable Integer id) {
        return formationService.getFormationById(id)
                .map(formation -> ResponseEntity.ok(formationMapper.toDTOWithModules(formation)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}") //tested
    public ResponseEntity<List<FormationDTO>> getFormationsByType(@PathVariable String type) {
        List<Formation> formations = formationService.getFormationsByType(type);
        return ResponseEntity.ok(formationMapper.toDTOList(formations));
    }

    @GetMapping("/search") //tested
    public ResponseEntity<List<FormationDTO>> searchFormations(@RequestParam String keyword) {
        List<Formation> formations = formationService.searchFormations(keyword);
        return ResponseEntity.ok(formationMapper.toDTOList(formations));
    }

    // ======== GESTION DES INSCRIPTIONS ========

    @GetMapping("/mes-formations/{collaborateurId}") //tested
    public ResponseEntity<List<CollaborateurFormationDTO>> getMesFormations(@PathVariable UUID collaborateurId) {
        List<CollaborateurFormation> inscriptions = collaborateurFormationService.getFormationsForCollaborateur(collaborateurId);
        return ResponseEntity.ok(collaborateurFormationMapper.toDTOList(inscriptions));
    }

    @GetMapping("/mes-formations/{collaborateurId}/terminées")
    public ResponseEntity<List<CollaborateurFormationDTO>> getMesFormationsTerminees(@PathVariable UUID collaborateurId) {
        List<CollaborateurFormation> inscriptions = collaborateurFormationService.getFormationsForCollaborateur(collaborateurId).stream()
                .filter(inscription -> inscription.getProgress().compareTo(new BigDecimal("100.00")) >= 0)
                .toList();
        return ResponseEntity.ok(collaborateurFormationMapper.toDTOList(inscriptions));
    }

    @GetMapping("/mes-formations/{collaborateurId}/en-cours")
    public ResponseEntity<List<CollaborateurFormationDTO>> getMesFormationsEnCours(@PathVariable UUID collaborateurId) {
        List<CollaborateurFormation> inscriptions = collaborateurFormationService.getFormationsForCollaborateur(collaborateurId).stream()
                .filter(inscription -> inscription.getProgress().compareTo(new BigDecimal("100.00")) < 0)
                .toList();
        return ResponseEntity.ok(collaborateurFormationMapper.toDTOList(inscriptions));
    }

    // ======== CONSULTATION DES MODULES ET CONTENUS ========

    @GetMapping("/{formationId}/modules")
    public ResponseEntity<List<ModuleDTO>> getModulesByFormation(@PathVariable Integer formationId) {
        List<Module> modules = moduleService.getModulesByFormationId(formationId);
        return ResponseEntity.ok(moduleMapper.toDTOList(modules));
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Integer id) {
        return moduleService.getModuleById(id)
                .map(module -> ResponseEntity.ok(moduleMapper.toDTOWithRelations(module)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/modules/{moduleId}/supports")
    public ResponseEntity<List<SupportDTO>> getSupportsByModule(@PathVariable Integer moduleId) {
        List<Support> supports = supportService.getSupportsByModuleId(moduleId);
        return ResponseEntity.ok(supportMapper.toDTOList(supports));
    }

    @GetMapping("/supports/{id}/download")
    public ResponseEntity<String> downloadSupport(@PathVariable Integer id) {
        String downloadUrl = supportService.getDownloadUrl(id);
        if (downloadUrl == null) {
            return ResponseEntity.notFound().build();
        }

        // Rediriger vers l'URL de téléchargement
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, downloadUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ======== QUIZ ET PROGRESSION ========

    @GetMapping("/modules/{moduleId}/quizzes")
    public ResponseEntity<List<QuizDTO>> getQuizzesByModule(@PathVariable Integer moduleId) {
        List<Quiz> quizzes = quizService.getQuizzesByModuleId(moduleId);
        return ResponseEntity.ok(quizMapper.toDTOList(quizzes));
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Integer id) {
        return quizService.getQuizById(id)
                .map(quiz -> ResponseEntity.ok(quizMapper.toDTOWithQuestions(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<QuizResultDTO> submitQuizAnswers(
            @PathVariable Integer quizId,
            @RequestBody Map<Integer, List<Integer>> userAnswers,
            @RequestParam UUID collaborateurId,
            @RequestParam Integer formationId) {

        // 1. Évaluer les réponses du quiz
        Map<String, Object> quizResult = quizService.evaluateQuizAnswers(quizId, userAnswers);
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);

        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Quiz quiz = quizOpt.get();
        QuizResultDTO resultDTO = quizMapper.toResultDTO(quiz, quizResult);

        // 2. Mettre à jour la progression du collaborateur
        // Supposons que chaque quiz complété ajoute 20% à la progression
        collaborateurFormationService.getFormationsForCollaborateur(collaborateurId).stream()
                .filter(inscription -> inscription.getFormation().getId().equals(formationId))
                .findFirst()
                .ifPresent(inscription -> {
                    BigDecimal currentProgress = inscription.getProgress();
                    BigDecimal newProgress = currentProgress.add(new BigDecimal("20.00"));

                    // Ne pas dépasser 100%
                    if (newProgress.compareTo(new BigDecimal("100.00")) > 0) {
                        newProgress = new BigDecimal("100.00");
                    }

                    collaborateurFormationService.updateProgress(collaborateurId, formationId, newProgress);
                });

        return ResponseEntity.ok(resultDTO);
    }

    @PutMapping("/progress/{collaborateurId}/{formationId}")
    public ResponseEntity<CollaborateurFormationDTO> updateProgress(
            @PathVariable UUID collaborateurId,
            @PathVariable Integer formationId,
            @RequestBody BigDecimal newProgress) {

        return collaborateurFormationService.updateProgress(collaborateurId, formationId, newProgress)
                .map(inscription -> ResponseEntity.ok(collaborateurFormationMapper.toDTO(inscription)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ======== CERTIFICATS ========

    @GetMapping("/certificat/{collaborateurId}/{formationId}")
    public ResponseEntity<ByteArrayResource> downloadCertificat(
            @PathVariable UUID collaborateurId,
            @PathVariable Integer formationId) {

        // Vérifier si la formation est complétée
        Optional<CollaborateurFormation> inscriptionOpt = collaborateurFormationService
                .getFormationsForCollaborateur(collaborateurId).stream()
                .filter(inscription -> inscription.getFormation().getId().equals(formationId))
                .findFirst();

        if (inscriptionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CollaborateurFormation inscription = inscriptionOpt.get();

        // Vérifier si la formation est complétée
        if (inscription.getProgress().compareTo(new BigDecimal("100.00")) < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Générer le certificat s'il n'est pas déjà généré
        if (!inscription.getIsCertificationGenerated()) {
            collaborateurFormationService.generateCertificate(collaborateurId, formationId);
        }

        // Créer un certificat simple en HTML
        String nomCollaborateur = inscription.getCollaborateur().getFirstName() + " " + inscription.getCollaborateur().getLastName();
        String titreFormation = inscription.getFormation().getTitre();
        LocalDate dateActuelle = LocalDate.now();

        String certificatHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Certificat de Formation</title>
                    <style>
                        body { font-family: Arial, sans-serif; text-align: center; margin: 50px; }
                        .certificat { border: 5px solid #2a579a; padding: 40px; max-width: 800px; margin: 0 auto; }
                        .titre { font-size: 24px; font-weight: bold; color: #2a579a; margin-bottom: 30px; }
                        .description { font-size: 18px; margin-bottom: 40px; }
                        .nom { font-size: 28px; font-weight: bold; color: #333; margin: 20px 0; }
                        .formation { font-size: 20px; font-style: italic; color: #2a579a; margin-bottom: 40px; }
                        .date { margin-top: 60px; font-size: 16px; }
                        .signature { margin-top: 80px; font-size: 16px; border-top: 1px solid #999; padding-top: 10px; width: 200px; margin: 80px auto 0; }
                    </style>
                </head>
                <body>
                    <div class="certificat">
                        <div class="titre">CERTIFICAT D'ACCOMPLISSEMENT</div>
                        <div class="description">Ce certificat est décerné à</div>
                        <div class="nom">%s</div>
                        <div class="description">pour avoir complété avec succès la formation</div>
                        <div class="formation">%s</div>
                        <div class="date">Date de délivrance : %s</div>
                        <div class="signature">Signature</div>
                    </div>
                </body>
                </html>
                """.formatted(nomCollaborateur, titreFormation, dateActuelle.toString());

        byte[] certificatBytes = certificatHtml.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(certificatBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificat_" + formationId + "_" + collaborateurId + ".html\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(certificatBytes.length)
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}