package com.competencesplateforme.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name="logout_tokens")
public class LogoutToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id ;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDate logoutDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(LocalDate logoutDate) {
        this.logoutDate = logoutDate;
    }
}
