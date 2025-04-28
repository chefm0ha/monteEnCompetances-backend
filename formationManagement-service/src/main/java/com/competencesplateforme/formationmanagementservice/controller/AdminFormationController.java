package com.competencesplateforme.formationmanagementservice.controller;

import com.competencesplateforme.formationmanagementservice.dto.*;
import com.competencesplateforme.formationmanagementservice.fileStorage.FileStorageService;
import com.competencesplateforme.formationmanagementservice.mapper.*;
import com.competencesplateforme.formationmanagementservice.model.*;
import com.competencesplateforme.formationmanagementservice.model.Module;
import com.competencesplateforme.formationmanagementservice.service.*;
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
            FileUploadMapper fileUploadMapper) {
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
    }

    // ======== GESTION DES FORMATIONS ========

    @GetMapping  //tested
    public ResponseEntity<List<FormationDTO>> getAllFormations() {
        List<Formation> formations = formationService.getAllFormations();
        List<FormationDTO> formationDTOs = formationMapper.toDTOList(formations);
        return ResponseEntity.ok(formationDTOs);
    }

    @GetMapping("/{id}")  //tested
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

    @GetMapping("/search")  //tested
    public ResponseEntity<List<FormationDTO>> searchFormations(@RequestParam String keyword) {
        List<Formation> formations = formationService.searchFormations(keyword);
        return ResponseEntity.ok(formationMapper.toDTOList(formations));
    }

    @PostMapping //tested
    public ResponseEntity<FormationDTO> createFormation(@RequestBody FormationDTO formationDTO) {
        Formation formation = formationMapper.toEntity(formationDTO);
        Formation savedFormation = formationService.createFormation(formation);
        return ResponseEntity.status(HttpStatus.CREATED).body(formationMapper.toDTO(savedFormation));
    }

    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //tested
    public ResponseEntity<FormationDTO> createFormationWithImage(
            @RequestPart("formation") FormationDTO formationDTO,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        Formation formation = formationMapper.toEntity(formationDTO);
        Formation savedFormation = formationService.createFormationWithImage(formation, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(formationMapper.toDTO(savedFormation));
    }

    @PutMapping("/{id}") //tested
    public ResponseEntity<FormationDTO> updateFormation(@PathVariable Integer id, @RequestBody FormationDTO formationDTO) {
        return formationService.updateFormation(id, formationMapper.toEntity(formationDTO))
                .map(updatedFormation -> ResponseEntity.ok(formationMapper.toDTO(updatedFormation)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //tested
    public ResponseEntity<FormationDTO> updateFormationWithImage(
            @PathVariable Integer id,
            @RequestPart("formation") FormationDTO formationDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        Formation formation = formationMapper.toEntity(formationDTO);
        return formationService.updateFormationWithImage(id, formation, imageFile)
                .map(updatedFormation -> ResponseEntity.ok(formationMapper.toDTO(updatedFormation)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Tested
    public ResponseEntity<Void> deleteFormation(@PathVariable Integer id) {
        boolean deleted = formationService.deleteFormation(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ======== GESTION DES MODULES ========

    @GetMapping("/{formationId}/modules") //tested
    public ResponseEntity<List<ModuleDTO>> getModulesByFormation(@PathVariable Integer formationId) {
        List<Module> modules = moduleService.getModulesByFormationId(formationId);
        return ResponseEntity.ok(moduleMapper.toDTOList(modules));
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
                .map(savedModule -> ResponseEntity.status(HttpStatus.CREATED).body(moduleMapper.toDTO(savedModule)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/modules/{id}") //tested
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Integer id, @RequestBody ModuleDTO moduleDTO) {
        return moduleService.updateModule(id, moduleMapper.toEntity(moduleDTO))
                .map(updatedModule -> ResponseEntity.ok(moduleMapper.toDTO(updatedModule)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/modules/{id}") //tested
    public ResponseEntity<Void> deleteModule(@PathVariable Integer id) {
        boolean deleted = moduleService.deleteModule(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
                .map(savedSupport -> ResponseEntity.status(HttpStatus.CREATED).body(supportMapper.toDTO(savedSupport)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/supports/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //tested
    public ResponseEntity<SupportDTO> updateSupport(
            @PathVariable Integer id,
            @RequestPart("support") SupportDTO supportDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        Support support = supportMapper.toEntity(supportDTO);
        return (file != null)
                ? supportService.updateSupportWithFile(id, support, file)
                .map(updatedSupport -> ResponseEntity.ok(supportMapper.toDTO(updatedSupport)))
                .orElse(ResponseEntity.notFound().build())
                : supportService.updateSupport(id, support)
                .map(updatedSupport -> ResponseEntity.ok(supportMapper.toDTO(updatedSupport)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/supports/{id}") //tested
    public ResponseEntity<Void> deleteSupport(@PathVariable Integer id) {
        boolean deleted = supportService.deleteSupport(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ======== GESTION DES QUIZ ========

    @GetMapping("/modules/{moduleId}/quizzes") // tested
    public ResponseEntity<List<QuizDTO>> getQuizzesByModule(@PathVariable Integer moduleId) {
        List<Quiz> quizzes = quizService.getQuizzesByModuleId(moduleId);
        return ResponseEntity.ok(quizMapper.toDTOList(quizzes));
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
                .map(savedQuiz -> ResponseEntity.status(HttpStatus.CREATED).body(quizMapper.toDTO(savedQuiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/quizzes/{id}")    // tested
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Integer id, @RequestBody QuizDTO quizDTO) {
        return quizService.updateQuiz(id, quizMapper.toEntity(quizDTO))
                .map(updatedQuiz -> ResponseEntity.ok(quizMapper.toDTO(updatedQuiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/quizzes/{id}")     // tested
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer id) {
        boolean deleted = quizService.deleteQuiz(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
                .map(savedQuestion -> ResponseEntity.status(HttpStatus.CREATED).body(questionMapper.toDTO(savedQuestion)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/questions/{id}") //tested
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Integer id, @RequestBody QuestionDTO questionDTO) {
        return questionService.updateQuestion(id, questionMapper.toEntity(questionDTO))
                .map(updatedQuestion -> ResponseEntity.ok(questionMapper.toDTO(updatedQuestion)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/questions/{id}") //tested
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        boolean deleted = questionService.deleteQuestion(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
                .map(savedChoix -> ResponseEntity.status(HttpStatus.CREATED).body(choixMapper.toDTO(savedChoix)))
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

        return ResponseEntity.status(HttpStatus.CREATED).body(choixMapper.toDTOList(savedChoix));
    }

    @PutMapping("/choix/{id}") //tested
    public ResponseEntity<ChoixDTO> updateChoix(@PathVariable Integer id, @RequestBody ChoixDTO choixDTO) {
        return choixService.updateChoix(id, choixMapper.toEntity(choixDTO))
                .map(updatedChoix -> ResponseEntity.ok(choixMapper.toDTO(updatedChoix)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/choix/{id}") //tested
    public ResponseEntity<Void> deleteChoix(@PathVariable Integer id) {
        boolean deleted = choixService.deleteChoix(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
                .map(inscription -> ResponseEntity.status(HttpStatus.CREATED).body(collaborateurFormationMapper.toDTO(inscription)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{formationId}/participants/{collaborateurId}") //tested
    public ResponseEntity<Void> desinscrireCollaborateur(
            @PathVariable Integer formationId,
            @PathVariable UUID collaborateurId) {

        boolean desinscrit = collaborateurFormationService.desinscrireCollaborateurDeFormation(collaborateurId, formationId);
        return desinscrit ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
}