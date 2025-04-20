package com.competencesplateforme.authservice.repository;

import com.competencesplateforme.authservice.model.LogoutToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LogoutTokenRepository extends JpaRepository<LogoutToken, UUID> {
    Optional<LogoutToken> findByToken(String token);
}
