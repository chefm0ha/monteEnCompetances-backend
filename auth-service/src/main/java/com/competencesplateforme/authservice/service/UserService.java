package com.competencesplateforme.authservice.service;

import com.competencesplateforme.authservice.dto.ChangePasswordDTO;
import com.competencesplateforme.authservice.dto.UpdateProfileDTO;
import com.competencesplateforme.authservice.model.User;
import com.competencesplateforme.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> updateUserProfile(String email, UpdateProfileDTO updateProfileDTO) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setFirstName(updateProfileDTO.getFirstName());
                    user.setLastName(updateProfileDTO.getLastName());

                    // Only update email if it's different and not already taken
                    if (!user.getEmail().equals(updateProfileDTO.getEmail())) {
                        if (userRepository.findByEmail(updateProfileDTO.getEmail()).isEmpty()) {
                            user.setEmail(updateProfileDTO.getEmail());
                        } else {
                            throw new RuntimeException("Email already exists");
                        }
                    }

                    return userRepository.save(user);
                });
    }

    public boolean changePassword(String email, ChangePasswordDTO changePasswordDTO) {
        // Check if new password matches confirmation
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            return false;
        }

        return userRepository.findByEmail(email)
                .map(user -> {
                    // Verify current password
                    if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
                        return false;
                    }

                    // Update to new password
                    user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
