package com.competencesplateforme.collaborateurmanagementservice.service;

import com.competencesplateforme.collaborateurmanagementservice.dto.CollaborateurDTO;
import com.competencesplateforme.collaborateurmanagementservice.mapper.CollaborateurMapper;
import com.competencesplateforme.collaborateurmanagementservice.model.Collaborateur;
import com.competencesplateforme.collaborateurmanagementservice.repository.CollaborateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollaborateurService {

    private final CollaborateurRepository collaborateurRepository;
    private final CollaborateurMapper collaborateurMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CollaborateurService(CollaborateurRepository collaborateurRepository,
                                CollaborateurMapper collaborateurMapper) {
        this.collaborateurRepository = collaborateurRepository;
        this.collaborateurMapper = collaborateurMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Crée un nouveau collaborateur
     * @param collaborateurDTO Les données du collaborateur à créer
     * @return Le DTO du collaborateur créé
     */
    @Transactional
    public CollaborateurDTO createCollaborateur(CollaborateurDTO collaborateurDTO) {
        // Encode the password before saving
        if (collaborateurDTO.getPassword() != null) {
            collaborateurDTO.setPassword(passwordEncoder.encode(collaborateurDTO.getPassword()));
        }
        collaborateurDTO.setRole("COLLABORATEUR");
        Collaborateur collaborateur = collaborateurMapper.toEntity(collaborateurDTO);
        collaborateur = collaborateurRepository.save(collaborateur);
        return collaborateurMapper.toDTO(collaborateur);
    }

    /**
     * Récupère tous les collaborateurs
     * @return La liste des collaborateurs
     */
    @Transactional(readOnly = true)
    public List<CollaborateurDTO> getAllCollaborateurs() {
        List<Collaborateur> collaborateurs = collaborateurRepository.findAll();
        return collaborateurMapper.toDTOList(collaborateurs);
    }

    /**
     * Recherche un collaborateur par son ID
     * @param id L'ID du collaborateur
     * @return Le DTO du collaborateur ou Optional.empty() si non trouvé
     */
    @Transactional(readOnly = true)
    public Optional<CollaborateurDTO> getCollaborateurById(UUID id) {
        return collaborateurRepository.findById(id)
                .map(collaborateurMapper::toDTO);
    }


    @Transactional(readOnly = true)
    public Optional<Collaborateur> getCollaborateurNotDtoById(UUID id) {
        return collaborateurRepository.findById(id);
    }

    /**
     * Recherche un collaborateur par son email
     * @param email L'email du collaborateur
     * @return Le DTO du collaborateur ou Optional.empty() si non trouvé
     */
    @Transactional(readOnly = true)
    public Optional<CollaborateurDTO> getCollaborateurByEmail(String email) {
        return collaborateurRepository.findByEmail(email)
                .map(collaborateurMapper::toDTO);
    }

    /**
     * Recherche des collaborateurs par leur poste
     * @param poste Le poste recherché
     * @return La liste des collaborateurs correspondants
     */
    @Transactional(readOnly = true)
    public List<CollaborateurDTO> getCollaborateursByPoste(String poste) {
        List<Collaborateur> collaborateurs = collaborateurRepository.findByPoste(poste);
        return collaborateurMapper.toDTOList(collaborateurs);
    }

    /**
     * Met à jour un collaborateur existant
     * @param id L'ID du collaborateur à mettre à jour
     * @param collaborateurDTO Les nouvelles données
     * @return Le DTO du collaborateur mis à jour ou Optional.empty() si non trouvé
     */
    @Transactional
    public Optional<CollaborateurDTO> updateCollaborateur(UUID id, CollaborateurDTO collaborateurDTO) {
        return collaborateurRepository.findById(id)
                .map(collaborateur -> {
                    collaborateurMapper.updateEntityFromDTO(collaborateurDTO, collaborateur);
                    Collaborateur updatedCollaborateur = collaborateurRepository.save(collaborateur);
                    return collaborateurMapper.toDTO(updatedCollaborateur);
                });
    }


    @Transactional
    public CollaborateurDTO updateCollaborateur(Collaborateur collaborateur) {
        collaborateurRepository.save(collaborateur) ;
        return collaborateurMapper.toDTO(collaborateur);
    }






    /**
     * Supprime un collaborateur
     * @param id L'ID du collaborateur à supprimer
     * @return true si supprimé, false si non trouvé
     */
    @Transactional
    public boolean deleteCollaborateur(UUID id) {
        if (collaborateurRepository.existsById(id)) {
            collaborateurRepository.deleteById(id);
            return true;
        }
        return false;
    }
}