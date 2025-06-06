package com.competencesplateforme.collaborateurmanagementservice.dto;

import java.util.UUID;

public class CollaborateurStatsDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String poste;
    private Double progression;

    public CollaborateurStatsDTO() {
    }

    public CollaborateurStatsDTO(UUID id, String firstName, String lastName, String email,
                                 String poste, Double progression) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.poste = poste;
        this.progression = progression;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public Double getProgression() {
        return progression;
    }

    public void setProgression(Double progression) {
        this.progression = progression;
    }
}