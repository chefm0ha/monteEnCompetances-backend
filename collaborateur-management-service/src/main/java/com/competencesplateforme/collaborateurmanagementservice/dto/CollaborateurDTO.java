package com.competencesplateforme.collaborateurmanagementservice.dto;

import java.util.UUID;

public class CollaborateurDTO {
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String poste;

    // Constructeurs
    public CollaborateurDTO() {
    }

    public CollaborateurDTO( String email, String password, String role,
                            String firstName, String lastName, String poste) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.poste = poste;
    }

    // Getters et Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }
}