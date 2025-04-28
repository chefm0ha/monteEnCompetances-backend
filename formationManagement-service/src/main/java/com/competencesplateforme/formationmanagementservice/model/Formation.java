package com.competencesplateforme.formationmanagementservice.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "formations")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String type;

    @Column(name = "lien_photo", columnDefinition = "TEXT")
    private String lienPhoto;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Module> modules = new HashSet<>();

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CollaborateurFormation> collaborateurFormations = new HashSet<>();

    // Constructeurs
    public Formation() {
    }

    public Formation(String titre, String description, String type, String lienPhoto) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.lienPhoto = lienPhoto;
    }

    // Getters et setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLienPhoto() {
        return lienPhoto;
    }

    public void setLienPhoto(String lienPhoto) {
        this.lienPhoto = lienPhoto;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public Set<CollaborateurFormation> getCollaborateurFormations() {
        return collaborateurFormations;
    }

    public void setCollaborateurFormations(Set<CollaborateurFormation> collaborateurFormations) {
        this.collaborateurFormations = collaborateurFormations;
    }

    // Méthodes utilitaires pour la gestion bidirectionnelle des relations
    public void addModule(Module module) {
        modules.add(module);
        module.setFormation(this);
    }

    public void removeModule(Module module) {
        modules.remove(module);
        module.setFormation(null);
    }
}