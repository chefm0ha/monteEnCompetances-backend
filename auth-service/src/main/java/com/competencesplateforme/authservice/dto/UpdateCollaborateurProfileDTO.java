package com.competencesplateforme.collaborateurmanagementservice.dto;

import jakarta.validation.constraints.Email;

public class UpdateCollaborateurProfileDTO {
    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;
    private String poste;

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
}