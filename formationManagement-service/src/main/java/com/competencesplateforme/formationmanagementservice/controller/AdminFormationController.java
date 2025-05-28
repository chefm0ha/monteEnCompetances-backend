package com.competencesplateforme.formationmanagementservice.controller;

import com.competencesplateforme.formationmanagementservice.dto.*;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import com.competencesplateforme.formationmanagementservice.mapper.*;
import com.competencesplateforme.formationmanagementservice.model.*;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/formations")
public class AdminFormationController {

    private final FormationService formationService;
    private final ModuleService moduleService;
    private final SupportService supportService;
    private final QuizService quizService;
    private final QuestionService questionService;
    private final ChoixService choixService;
    private final CollaborateurFormationService collaborateurFormationService;
    private final FileStorageService fileStorageService;

    private final FormationMapper formationMapper;
    private final ModuleMapper moduleMapper;
    private final SupportMapper supportMapper;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final ChoixMapper choixMapper;
    private final CollaborateurFormationMapper collaborateurFormationMapper;
    private final FileUploadMapper fileUploadMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminFormationController.class);
    private final NotificationService notificationService;


    @Autowired
    public AdminFormationController(
            FormationService formationService,
            ModuleService moduleService,
            SupportService supportService,
            QuizService quizService,
            QuestionService questionService,
            ChoixService choixService,
            CollaborateurFormationService collaborateurFormationService,
            FileStorageService fileStorageService,
            FormationMapper formationMapper,
            ModuleMapper moduleMapper,
            SupportMapper supportMapper,
            QuizMapper quizMapper,
            QuestionMapper questionMapper,
            ChoixMapper choixMapper,
            CollaborateurFormationMapper collaborateurFormationMapper,
            FileUploadMapper fileUploadMapper ,
            NotificationService notificationService) {
        this.formationService = formationService;
        this.moduleService = moduleService;
        this.supportService = supportService;
        this.quizService = quizService;
        this.questionService = questionService;
        this.choixService = choixService;
        this.collaborateurFormationService = collaborateurFormationService;
        this.fileStorageService = fileStorageService;
        this.formationMapper = formationMapper;
        this.moduleMapper = moduleMapper;
        this.supportMapper = supportMapper;
        this.quizMapper = quizMapper;
        this.questionMapper = questionMapper;
        this.choixMapper = choixMapper;
        this.collaborateurFormationMapper = collaborateurFormationMapper;
        this.fileUploadMapper = fileUploadMapper;
        this.notificationService = notificationService;
    }

    // ======== GESTION DES FORMATIONS ========

    @GetMapping  //tested
    public ResponseEntity<List<FormationDTO>> getAllFormations() {
        List<Formation> formations = formationService.getAllFormations();
        List<FormationDTO> formationDTOs = formationMapper.toDTOList(formations);
        return ResponseEntity.ok(formationDTOs);
    }

    @GetMapping("/with-module-count")
    public ResponseEntity<List<FormationWithModuleCountDTO>> getAllFormationsWithModuleCount() {
        List<FormationWithModuleCountDTO> formations = formationService.getAllFormationsWithModuleCount();
        return ResponseEntity.ok(formations);
    }

    @GetMapping("/{id}")  //tested
    public ResponseEntity<FormationDTO> getFormationById(@PathVariable Integer id) {
        return formationService.getFormationById(id)
                .map(formation -> ResponseEntity.ok(formationMapper.toDTOWithModules(formation)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{formationId}/modules") //tested
    public ResponseEntity<List<ModuleDTO>> getModulesByFormation(@PathVariable Integer formationId) {
        List<Module> modules = moduleService.getModulesByFormationId(formationId);
        return ResponseEntity.ok(moduleMapper.toDTOList(modules));
    }

    @GetMapping("/type/{type}") //tested
    public ResponseEntity<List<FormationDTO>> getFormationsByType(@PathVariable String type) {
        List<Formation> formations = formationService.getFormationsByType(type);
        return ResponseEntity.ok(formationMapper.toDTOList(formations));
    }

    @GetMapping("/search")  //tested
    public ResponseEntity<List<FormationDTO>> searchFormations(@RequestParam String keyword) {
        List<Formation> formations = formationService.searchFormations(keyword);
        return ResponseEntity.ok(formationMapper.toDTOList(formations));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FormationDTO> createFormation(
            @RequestPart("formation") FormationDTO formationDTO,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        Formation formation = formationMapper.toEntity(formationDTO);
        Formation savedFormation = formationService.createFormationWithImage(formation, imageFile);
        // Envoyer notification au manager
        sendAdminNotification(
                "Nouvelle Formation ajoutée",
                "Le Formation " + savedFormation.getTitre()
                        + " " +
                        " a été ajoutée ."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(formationMapper.toDTO(savedFormation));
    }

    /**
     * Met à jour une formation sans changer l'image
     */
    @PutMapping("/{id}")
    public ResponseEntity<FormationDTO> updateFormation(
            @PathVariable Integer id,
            @RequestBody FormationDTO formationDTO) {

        Formation formation = formationMapper.toEntity(formationDTO);

        return formationService.updateFormation(id, formation)
                .map(updatedFormation -> {
                    // Envoyer notification à l'admin après mise à jour réussie
                    sendAdminNotification(
                            "Formation mise à jour",
                            "La formation '" + updatedFormation.getTitre() + "' a été mise à jour avec succès (sans modification d'image)."
                    );

                    return ResponseEntity.ok(formationMapper.toDTO(updatedFormation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Met à jour une formation avec une nouvelle image
     */
    @PutMapping(value = "/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FormationDTO> updateFormationWithImage(
            @PathVariable Integer id,
            @RequestPart("formation") FormationDTO formationDTO,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        Formation formation = formationMapper.toEntity(formationDTO);

        return formationService.updateFormationWithImage(id, formation, imageFile)
                .map(updatedFormation -> {
                    // Envoyer notification à l'admin après mise à jour réussie
                    sendAdminNotification(
                            "Formation mise à jour avec image",
                            String.format("La formation '%s' a été mise à jour avec succès.\n" +
                                            "Nouvelle image: %s",
                                    updatedFormation.getTitre(),
                                    imageFile.getOriginalFilename())
                    );

                    return ResponseEntity.ok(formationMapper.toDTO(updatedFormation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Tested
    public ResponseEntity<Void> deleteFormation(@PathVariable Integer id) {
        Optional<Formation> formation = formationService.getFormationById(id);
        boolean deleted = formationService.deleteFormation(id);
        if(deleted && formation.isPresent()) {
            sendAdminNotification(
                    "Formation Supprimé",
                    "La formation '" + formation.get().getTitre() + "' a été supprimée."
            );
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<List<FormationSummaryDTO>> getAllFormationsSummary() {
        List<Formation> formations = formationService.getAllFormationsWithModules();
        List<FormationSummaryDTO> formationSummaryDTOs = formationMapper.toSummaryDTOList(formations);
        return ResponseEntity.ok(formationSummaryDTOs);
    }

    // ======== GESTION DES MODULES ========


    @GetMapping("/all-modules")
    public ResponseEntity<List<ModuleWithFormationDTO>> getAllModulesWithFormation() {
        List<ModuleWithFormationDTO> modules = moduleService.getAllModulesWithFormationAndCounts();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/modules/{id}")  // tested
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Integer id) {
        return moduleService.getModuleById(id)
                .map(module -> ResponseEntity.ok(moduleMapper.toDTOWithRelations(module)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{formationId}/modules")  //tested
    public ResponseEntity<ModuleDTO> createModule(@PathVariable Integer formationId, @RequestBody ModuleDTO moduleDTO) {
        Module module = moduleMapper.toEntity(moduleDTO);
        return moduleService.createModule(formationId, module)
                .map(savedModule -> {
                    // Envoyer notification à l'admin après création réussie du module
                    sendAdminNotification(
                            "Nouveau module ajouté",
                            "Le module '" + savedModule.getTitre() + "' a été ajouté à la formation ."
                    );

                    return ResponseEntity.status(HttpStatus.CREATED).body(moduleMapper.toDTO(savedModule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/modules/{id}") //tested
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Integer id, @RequestBody ModuleDTO moduleDTO) {
        return moduleService.updateModule(id, moduleMapper.toEntity(moduleDTO))
                .map(updatedModule -> {
                    // Notification à l'admin après mise à jour réussie
                    sendAdminNotification(
                            "Module mis à jour",
                            "Le module '" + updatedModule.getTitre() + "' (ID: " + id + ") a été mis à jour avec succès."
                    );

                    return ResponseEntity.ok(moduleMapper.toDTO(updatedModule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/modules/{id}") //tested
    public ResponseEntity<Void> deleteModule(@PathVariable Integer id) {
        // Récupérer les infos du module avant suppression (optionnel)
        Optional<Module> moduleToDelete = moduleService.getModuleById(id);

        boolean deleted = moduleService.deleteModule(id);

        if (deleted) {
            // Notification à l'admin après suppression réussie
            String moduleName = moduleToDelete.map(Module::getTitre).orElse("Module inconnu");
            sendAdminNotification(
                    "Module supprimé",
                    "Le module '" + moduleName + "' (ID: " + id + ") a été supprimé du système."
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ======== GESTION DES SUPPORTS ========

    @GetMapping("/modules/{moduleId}/supports") //tested
    public ResponseEntity<List<SupportDTO>> getSupportsByModule(@PathVariable Integer moduleId) {
        List<Support> supports = supportService.getSupportsByModuleId(moduleId);
        return ResponseEntity.ok(supportMapper.toDTOList(supports));
    }

    @PostMapping(value = "/modules/{moduleId}/supports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)   //Tested
    public ResponseEntity<SupportDTO> createSupport(
            @PathVariable Integer moduleId,
            @RequestPart("support") SupportDTO supportDTO,
            @RequestPart("file") MultipartFile file) throws IOException {

        Support support = supportMapper.toEntity(supportDTO);
        return supportService.createSupportWithFile(moduleId, support, file)
                .map(savedSupport -> {
                    // Notification à l'admin après création réussie
                    sendAdminNotification(
                            "Nouveau support ajouté",
                            String.format("Support '%s' ajouté au module (ID: %d).\n" +
                                            "Fichier: %s\n" +
                                            "Type: %s",
                                    savedSupport.getTitre(),
                                    moduleId,
                                    file.getOriginalFilename(),
                                    savedSupport.getType() != null ? savedSupport.getType() : "Non spécifié")
                    );

                    return ResponseEntity.status(HttpStatus.CREATED).body(supportMapper.toDTO(savedSupport));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/supports/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //tested
    public ResponseEntity<SupportDTO> updateSupport(
            @PathVariable Integer id,
            @RequestPart("support") SupportDTO supportDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        Support support = supportMapper.toEntity(supportDTO);

        if (file != null) {
            return supportService.updateSupportWithFile(id, support, file)
                    .map(updatedSupport -> {
                        // Notification pour mise à jour avec nouveau fichier
                        sendAdminNotification(
                                "Support mis à jour avec fichier",
                                String.format("Support '%s' (ID: %d) mis à jour.\n" +
                                                "Nouveau fichier: %s",
                                        updatedSupport.getTitre(),
                                        id,
                                        file.getOriginalFilename())
                        );

                        return ResponseEntity.ok(supportMapper.toDTO(updatedSupport));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return supportService.updateSupport(id, support)
                    .map(updatedSupport -> {
                        // Notification pour mise à jour sans fichier
                        sendAdminNotification(
                                "Support mis à jour",
                                String.format("Support '%s' (ID: %d) mis à jour (métadonnées uniquement).",
                                        updatedSupport.getTitre(),
                                        id)
                        );

                        return ResponseEntity.ok(supportMapper.toDTO(updatedSupport));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @DeleteMapping("/supports/{id}") //tested
    public ResponseEntity<Void> deleteSupport(@PathVariable Integer id) {
        // Récupérer les infos du support avant suppression (optionnel)
        Optional<Support> supportToDelete = supportService.getSupportById(id);

        boolean deleted = supportService.deleteSupport(id);

        if (deleted) {
            // Notification à l'admin après suppression réussie
            String supportName = supportToDelete.map(Support::getTitre).orElse("Support inconnu");
            sendAdminNotification(
                    "Support supprimé",
                    String.format("Le support '%s' (ID: %d) a été supprimé du système.",
                            supportName,
                            id)
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ======== GESTION DES QUIZ ========

    @GetMapping("/modules/{moduleId}/quizzes")
    public ResponseEntity<List<QuizDTO>> getQuizzesByModuleWithQuestions(@PathVariable Integer moduleId) {
        List<Quiz> quizzes = quizService.getQuizzesByModuleIdWithQuestions(moduleId);
        List<QuizDTO> quizDTOs = quizMapper.toDTOListWithQuestions(quizzes);
        return ResponseEntity.ok(quizDTOs);
    }

    @GetMapping("/quizzes/{id}") //tested
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Integer id) {
        return quizService.getQuizById(id)
                .map(quiz -> ResponseEntity.ok(quizMapper.toDTOWithQuestions(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/modules/{moduleId}/quizzes")     //tested
    public ResponseEntity<QuizDTO> createQuiz(@PathVariable Integer moduleId, @RequestBody QuizDTO quizDTO) {
        Quiz quiz = quizMapper.toEntity(quizDTO);
        return quizService.createQuiz(moduleId, quiz)
                .map(savedQuiz -> {
                    // Notification à l'admin après création réussie
                    sendAdminNotification(
                            "Nouveau quiz créé",
                            String.format("Quiz '%s' ajouté au module (ID: %d).\n" +
                                            "Quiz ID: %d" +
                                    savedQuiz.getTitre(),
                                    moduleId,
                                    savedQuiz.getId())
                    );
                    return ResponseEntity.status(HttpStatus.CREATED).body(quizMapper.toDTO(savedQuiz));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/quizzes/{id}")     // tested
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer id) {
        // Récupérer les infos du quiz avant suppression (optionnel)
        Optional<Quiz> quizToDelete = quizService.getQuizById(id);

        boolean deleted = quizService.deleteQuiz(id);

        if (deleted) {
            // Notification à l'admin après suppression réussie
            String quizName = quizToDelete.map(Quiz::getTitre).orElse("Quiz inconnu");
            sendAdminNotification(
                    "Quiz supprimé",
                    String.format("Le quiz '%s' (ID: %d) a été supprimé du système.",
                            quizName,
                            id)
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ======== GESTION DES QUESTIONS ========

    @GetMapping("/quizzes/{quizId}/questions") //tested
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuiz(@PathVariable Integer quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questionMapper.toDTOList(questions));
    }

    @PostMapping("/quizzes/{quizId}/questions") //tested
    public ResponseEntity<QuestionDTO> createQuestion(@PathVariable Integer quizId, @RequestBody QuestionDTO questionDTO) {
        Question question = questionMapper.toEntity(questionDTO);
        return questionService.createQuestion(quizId, question)
                .map(savedQuestion -> {
                    // Notification à l'admin après création réussie
                    sendAdminNotification(
                            "Nouvelle question ajoutée",
                            String.format("Question ajoutée au quiz ( titre : %s).\n" +
                                            "contenu : %s",
                                    savedQuestion.getQuiz().getTitre() ,
                                    savedQuestion.getContenu())
                    );

                    return ResponseEntity.status(HttpStatus.CREATED).body(questionMapper.toDTO(savedQuestion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/questions/{id}") //tested
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Integer id, @RequestBody QuestionDTO questionDTO) {
        return questionService.updateQuestion(id, questionMapper.toEntity(questionDTO))
                .map(updatedQuestion -> {
                    // Notification à l'admin après mise à jour réussie
                    sendAdminNotification(
                            "Question mise à jour",
                            String.format("La question (ID: %d) a été mise à jour avec succès.",
                                    id)
                    );

                    return ResponseEntity.ok(questionMapper.toDTO(updatedQuestion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/questions/{id}") //tested
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        // Récupérer les infos de la question avant suppression (optionnel)
        Optional<Question> questionToDelete = questionService.getQuestionById(id);

        boolean deleted = questionService.deleteQuestion(id);

        if (deleted) {
            // Notification à l'admin après suppression réussie
            String questionText = questionToDelete.map(Question::getContenu).orElse("Question inconnue");
            sendAdminNotification(
                    "Question supprimée",
                    String.format("La question '%s' (ID: %d) a été supprimée du système.",
                            questionText.length() > 50 ? questionText.substring(0, 50) + "..." : questionText,
                            id)
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ======== GESTION DES CHOIX ========

    @GetMapping("/questions/{questionId}/choix") //tested
    public ResponseEntity<List<ChoixDTO>> getChoixByQuestion(@PathVariable Integer questionId) {
        List<Choix> choix = choixService.getChoixByQuestionId(questionId);
        return ResponseEntity.ok(choixMapper.toDTOList(choix));
    }

    @PostMapping("/questions/{questionId}/choix") //tested
    public ResponseEntity<ChoixDTO> createChoix(@PathVariable Integer questionId, @RequestBody ChoixDTO choixDTO) {
        Choix choix = choixMapper.toEntity(choixDTO);
        return choixService.createChoix(questionId, choix)
                .map(savedChoix -> {
                    // Notification à l'admin après création réussie
                    sendAdminNotification(
                            "Nouveau choix de réponse ajouté",
                            String.format("Choix '%s' ajouté à la question (ID: %d).\n" +
                                            "Choix ID: %d\n" +
                                            "Correct: %s",
                                    savedChoix.getContenu(),
                                    questionId,
                                    savedChoix.getId(),
                                    savedChoix.getEstCorrect() != null && savedChoix.getEstCorrect() ? "Oui" : "Non")
                    );

                    return ResponseEntity.status(HttpStatus.CREATED).body(choixMapper.toDTO(savedChoix));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/questions/{questionId}/choix/multiple") //tested
    public ResponseEntity<List<ChoixDTO>> createMultipleChoix(@PathVariable Integer questionId, @RequestBody List<ChoixDTO> choixDTOs) {
        List<Choix> choixList = choixDTOs.stream()
                .map(choixMapper::toEntity)
                .collect(Collectors.toList());

        List<Choix> savedChoix = choixService.createMultipleChoix(questionId, choixList);

        if (savedChoix.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Notification à l'admin après création réussie
        sendAdminNotification(
                "Choix multiples ajoutés",
                String.format("%d choix de réponses ont été ajoutés à la question (ID: %d).",
                        savedChoix.size(),
                        questionId)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(choixMapper.toDTOList(savedChoix));
    }

    @PutMapping("/choix/{id}") //tested
    public ResponseEntity<ChoixDTO> updateChoix(@PathVariable Integer id, @RequestBody ChoixDTO choixDTO) {
        return choixService.updateChoix(id, choixMapper.toEntity(choixDTO))
                .map(updatedChoix -> {
                    // Notification à l'admin après mise à jour réussie
                    sendAdminNotification(
                            "Choix de réponse mis à jour",
                            String.format("Le choix '%s' (ID: %d) a été mis à jour.",
                                    updatedChoix.getContenu(),
                                    id)
                    );

                    return ResponseEntity.ok(choixMapper.toDTO(updatedChoix));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/choix/{id}") //tested
    public ResponseEntity<Void> deleteChoix(@PathVariable Integer id) {
        // Récupérer les infos du choix avant suppression (optionnel)
        Optional<Choix> choixToDelete = choixService.getChoixById(id);

        boolean deleted = choixService.deleteChoix(id);

        if (deleted) {
            // Notification à l'admin après suppression réussie
            String choixText = choixToDelete.map(Choix::getContenu).orElse("Choix inconnu");
            sendAdminNotification(
                    "Choix de réponse supprimé",
                    String.format("Le choix '%s' (ID: %d) a été supprimé du système.",
                            choixText.length() > 40 ? choixText.substring(0, 40) + "..." : choixText,
                            id)
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ======== QUIZ EVALUATION ========

    @PostMapping("/quizzes/{quizId}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateQuiz(
            @PathVariable Integer quizId,
            @RequestBody Map<Integer, List<Integer>> userAnswers) {

        try {
            Map<String, Object> result = quizService.evaluateQuizAnswers(quizId, userAnswers);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

// ======== UPDATED QUIZ MANAGEMENT ========

    @PutMapping("/quizzes/{id}")    // Updated version
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Integer id, @RequestBody QuizDTO quizDTO) {
        return quizService.updateCompleteQuiz(id, quizMapper.toEntity(quizDTO))
                .map(updatedQuiz -> {
                    // Notification à l'admin après mise à jour réussie
                    sendAdminNotification(
                            "Quiz mis à jour",
                            String.format("Le quiz '%s' a été mis à jour avec succès.",
                                    updatedQuiz.getTitre())
                    );

                    return ResponseEntity.ok(quizMapper.toDTOWithQuestions(updatedQuiz));
                })
                .orElse(ResponseEntity.notFound().build());
    }

// ======== COMPLETE QUIZ OPERATIONS ========

    @PostMapping("/modules/{moduleId}/quizzes/complete")
    public ResponseEntity<QuizDTO> createCompleteQuiz(
            @PathVariable Integer moduleId,
            @RequestBody QuizDTO quizDTO) {

        try {
            Quiz quiz = quizMapper.toEntity(quizDTO);
            Optional<Quiz> savedQuiz = quizService.createCompleteQuiz(moduleId, quiz, quizDTO.getQuestions());

            return savedQuiz
                    .map(q -> {
                        // Notification à l'admin après création réussie
                        int questionCount = quizDTO.getQuestions() != null ? quizDTO.getQuestions().size() : 0;
                        sendAdminNotification(
                                "Quiz complet créé",
                                String.format("Quiz complet '%s' créé dans le module (: %s).\n" +
                                                "Quiz ID: %d\n" +
                                                "Nombre de questions: %d\n" ,
                                        q.getTitre(),
                                        moduleService.getModuleById(moduleId).get().getTitre(),
                                        q.getId(),
                                        questionCount)
                        );

                        return ResponseEntity.status(HttpStatus.CREATED).body(quizMapper.toDTOWithQuestions(q));
                    })
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/quizzes/{id}/complete")
    public ResponseEntity<QuizDTO> updateCompleteQuiz(
            @PathVariable Integer id,
            @RequestBody QuizDTO quizDTO) {

        try {
            Quiz quiz = quizMapper.toEntity(quizDTO);
            Optional<Quiz> updatedQuiz = quizService.updateCompleteQuiz(id, quiz, quizDTO.getQuestions());

            return updatedQuiz
                    .map(q -> {
                        // Notification à l'admin après mise à jour réussie
                        int questionCount = quizDTO.getQuestions() != null ? quizDTO.getQuestions().size() : 0;
                        sendAdminNotification(
                                "Quiz complet mis à jour",
                                String.format("Quiz complet '%s' (ID: %d) mis à jour avec succès.\n" +
                                                "Nombre de questions: %d\n",
                                        q.getTitre(),
                                        id,
                                        questionCount)
                        );

                        return ResponseEntity.ok(quizMapper.toDTOWithQuestions(q));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ======== GESTION DES INSCRIPTIONS ========

    @GetMapping("/{formationId}/participants") //tested
    public ResponseEntity<List<CollaborateurFormationDTO>> getParticipantsByFormation(@PathVariable Integer formationId) {
        List<CollaborateurFormation> inscriptions = collaborateurFormationService.getCollaborateursForFormation(formationId);
        return ResponseEntity.ok(collaborateurFormationMapper.toDTOList(inscriptions));
    }

    @PostMapping("/{formationId}/participants/{collaborateurId}") //tested
    public ResponseEntity<CollaborateurFormationDTO> inscrireCollaborateur(
            @PathVariable Integer formationId,
            @PathVariable UUID collaborateurId) {

        return collaborateurFormationService.inscrireCollaborateurAFormation(collaborateurId, formationId)
                .map(inscription -> {
                    // Récupérer les infos de la formation (si possible)
                    String formationTitle = formationService.getFormationById(formationId).get().getTitre() ;

                    // Notification au collaborateur inscrit
                    sendNotification(
                            "Inscription à une formation",
                            String.format("Félicitations ! Vous avez été inscrit(e) à la formation '%s'. " +
                                            "Vous pouvez maintenant accéder au contenu de formation dans votre espace.",
                                    formationTitle),
                            collaborateurId
                    );

                    // Notification à l'administrateur
                    sendAdminNotification(
                            "Nouvelle inscription formation",
                            String.format("Le collaborateur (ID: %s) a été inscrit à la formation '%s' (ID: %d).",
                                    collaborateurId,
                                    formationTitle,
                                    formationId)
                    );

                    return ResponseEntity.status(HttpStatus.CREATED).body(collaborateurFormationMapper.toDTO(inscription));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{formationId}/participants/{collaborateurId}") //tested
    public ResponseEntity<Void> desinscrireCollaborateur(
            @PathVariable Integer formationId,
            @PathVariable UUID collaborateurId) {

        // Récupérer les infos avant désinscription
        String formationTitle = formationService.getFormationById(formationId).get().getTitre() ;

        boolean desinscrit = collaborateurFormationService.desinscrireCollaborateurDeFormation(collaborateurId, formationId);

        if (desinscrit) {
            // Notification au collaborateur désinscrit
            sendNotification(
                    "Désinscription de formation",
                    String.format("Vous avez été désinscrit(e) de la formation '%s'. " +
                                    "Vous n'avez plus accès au contenu de cette formation.",
                            formationTitle),
                    collaborateurId
            );

            // Notification à l'administrateur
            sendAdminNotification(
                    "Désinscription formation",
                    String.format("Le collaborateur (ID: %s) a été désinscrit de la formation '%s' (ID: %d).",
                            collaborateurId,
                            formationTitle,
                            formationId)
            );

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{formationId}/stats") //tested
    public ResponseEntity<Map<String, Object>> getFormationStats(@PathVariable Integer formationId) {
        Long nombreParticipants = collaborateurFormationService.countParticipantsByFormation(formationId);
        BigDecimal progressionMoyenne = collaborateurFormationService.getAverageProgressByFormation(formationId);

        Map<String, Object> stats = Map.of(
                "formationId", formationId,
                "nombreParticipants", nombreParticipants,
                "progressionMoyenne", progressionMoyenne
        );

        return ResponseEntity.ok(stats);
    }



    //------- Notification Functions ---------------//

    /**
     * Envoie une notification via le service de notifications
     */
    private void sendNotification(String titre, String contenu, UUID userId) {
        try {
            notificationService.createNotification(titre, contenu, userId);
        } catch (Exception e) {
            // Log l'erreur mais ne bloque pas le processus principal
            logger.warn("Impossible d'envoyer la notification à l'utilisateur {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Envoie une notification à plusieurs utilisateurs
     */
    private void sendNotification(String titre, String contenu, List<UUID> userIds) {
        try {
            notificationService.createNotification(titre, contenu, userIds);
        } catch (Exception e) {
            logger.warn("Impossible d'envoyer la notification à {} utilisateurs: {}", userIds.size(), e.getMessage());
        }
    }

    // Dans votre fonction privée du contrôleur
    private void sendAdminNotification(String titre, String contenu) {
        try {
            notificationService.createAdminNotification(titre, contenu);
            logger.debug("Notification admin envoyée: {}", titre);
        } catch (Exception e) {
            logger.warn("Impossible d'envoyer la notification admin: {}", e.getMessage());
        }
    }


    //---------- chatbot --------------//
    @GetMapping("/complete")
    public ResponseEntity<List<FormationDTO>> getAllFormationsComplete() {
        List<Formation> formations = formationService.getAllFormationsWithModules();
        List<FormationDTO> formationDTOs = formations.stream()
                .map(formationMapper::toDTOWithModules)
                .collect(Collectors.toList());
        return ResponseEntity.ok(formationDTOs);
    }

}