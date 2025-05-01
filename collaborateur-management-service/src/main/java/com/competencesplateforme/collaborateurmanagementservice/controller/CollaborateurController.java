package com.competencesplateforme.collaborateurmanagementservice.controller;

import com.competencesplateforme.collaborateurmanagementservice.dto.CollaborateurDTO;
import com.competencesplateforme.collaborateurmanagementservice.model.Collaborateur;
import com.competencesplateforme.collaborateurmanagementservice.service.CollaborateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/collaborateurs")
@Tag(name = "Collaborateur-management", description = "API for managing Collaborateur")
public class CollaborateurController {

    private final CollaborateurService collaborateurService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CollaborateurController(CollaborateurService collaborateurService ,PasswordEncoder passwordEncoder) {
        this.collaborateurService = collaborateurService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crée un nouveau collaborateur
     * @param collaborateurDTO Les données du collaborateur
     * @return Le collaborateur créé avec son ID
     */
    @PostMapping
    @Operation(summary = "Creating Collaborateur as user")
    public ResponseEntity<?> createCollaborateur(@RequestBody CollaborateurDTO collaborateurDTO) {
        try {
            CollaborateurDTO createdCollaborateur = collaborateurService.createCollaborateur(collaborateurDTO);
            return new ResponseEntity<>(createdCollaborateur, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Email " + collaborateurDTO.getEmail() + " already exists.";
            return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
        }
    }

    /**
     * Récupère tous les collaborateurs
     * @return La liste des collaborateurs
     */
    @GetMapping
    @Operation(summary = "Get All Collaborateurs")
    public ResponseEntity<List<CollaborateurDTO>> getAllCollaborateurs() {
        List<CollaborateurDTO> collaborateurs = collaborateurService.getAllCollaborateurs();
        return new ResponseEntity<>(collaborateurs, HttpStatus.OK);
    }

    /**
     * Récupère un collaborateur par son ID
     * @param id L'ID du collaborateur
     * @return Le collaborateur ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Collaborateur by ID")
    public ResponseEntity<CollaborateurDTO> getCollaborateurById(@PathVariable UUID id) {
        return collaborateurService.getCollaborateurById(id)
                .map(collaborateur -> new ResponseEntity<>(collaborateur, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Recherche un collaborateur par son email
     * @param email L'email du collaborateur
     * @return Le collaborateur ou 404 si non trouvé
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get Collaborateur by Email")
    public ResponseEntity<CollaborateurDTO> getCollaborateurByEmail(@PathVariable String email) {
        return collaborateurService.getCollaborateurByEmail(email)
                .map(collaborateur -> new ResponseEntity<>(collaborateur, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Recherche des collaborateurs par leur poste
     * @param poste Le poste recherché
     * @return La liste des collaborateurs correspondants
     */
    @GetMapping("/poste/{poste}")
    @Operation(summary = "Get Collaborateur by Poste")
    public ResponseEntity<List<CollaborateurDTO>> getCollaborateursByPoste(@PathVariable String poste) {
        List<CollaborateurDTO> collaborateurs = collaborateurService.getCollaborateursByPoste(poste);
        return new ResponseEntity<>(collaborateurs, HttpStatus.OK);
    }

    /**
     * Met à jour un collaborateur existant
     * @param id L'ID du collaborateur
     * @param collaborateurDTO Les nouvelles données
     * @return Le collaborateur mis à jour ou 404 si non trouvé
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Collaborateur - By ID")
    public ResponseEntity<?> updateCollaborateur(
            @PathVariable UUID id,
            @RequestBody CollaborateurDTO collaborateurDTO) {

        Optional<Collaborateur> optionalCollaborateur = collaborateurService.getCollaborateurNotDtoById(id);

        if (optionalCollaborateur.isPresent()) {
            Collaborateur collaborateur = optionalCollaborateur.get();

            if (collaborateurDTO.getEmail() != null) {
                collaborateur.setEmail(collaborateurDTO.getEmail());
            }

            if (collaborateurDTO.getFirstName() != null) {
                collaborateur.setFirstName(collaborateurDTO.getFirstName());
            }

            if (collaborateurDTO.getLastName() != null) {
                collaborateur.setLastName(collaborateurDTO.getLastName());
            }

            if (collaborateurDTO.getPoste() != null) {
                collaborateur.setPoste(collaborateurDTO.getPoste());
            }

            if (collaborateurDTO.getPassword() != null) {
                collaborateur.setPassword(passwordEncoder.encode(collaborateurDTO.getPassword()));
            }

            try {
                CollaborateurDTO updated = collaborateurService.updateCollaborateur(collaborateur);
                return new ResponseEntity<>(updated, HttpStatus.OK);
            } catch (DataIntegrityViolationException ex) {
                // Check if it's about the email
                String errorMessage = "Email " + collaborateurDTO.getEmail() + " already exists.";
                return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Supprime un collaborateur
     * @param id L'ID du collaborateur
     * @return 204 No Content si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Collaborateur - By ID")
    public ResponseEntity<Void> deleteCollaborateur(@PathVariable UUID id) {
        boolean deleted = collaborateurService.deleteCollaborateur(id);
        return deleted
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}