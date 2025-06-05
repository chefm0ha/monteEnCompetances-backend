package com.competencesplateforme.authservice.controller;

import com.competencesplateforme.authservice.dto.*;
import com.competencesplateforme.authservice.mapper.UserMapper;
import com.competencesplateforme.authservice.service.AuthService;
import com.competencesplateforme.authservice.service.UserService;
import com.competencesplateforme.authservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for managing Authentication")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserService userService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO loginRequestDTO
    ){
        Map<String , Object> authMap = authService.authenticate(loginRequestDTO);

        if (authMap.get("user") == null || authMap.get("token") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else{
            UserDTO user = (UserDTO) authMap.get("user");
            String token = authMap.get("token").toString();
            return ResponseEntity.ok(new LoginResponseDTO(user , token));
        }
    }


    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // validateAdmin
    @Operation(summary = "Validate Token as 'Administrateur' ")
    @GetMapping("/validate-admin")
    public ResponseEntity<Void> validateAdminToken(
            @RequestHeader("Authorization") String authHeader) {

        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!authService.validateToken(authHeader.substring(7))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else {
            return authService.isAdmin(authHeader.substring(7))
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // validateCollaborateur
    @Operation(summary = "Validate Token as 'Collaborateur' ")
    @GetMapping("/validate-collaborateur")
    public ResponseEntity<Void> validateCollaborateurToken(
            @RequestHeader("Authorization") String authHeader) {

        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!authService.validateToken(authHeader.substring(7))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else {
            return authService.isCollaborator(authHeader.substring(7))
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Logout")
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return authService.logoutToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile information")
    public ResponseEntity<UserDTO> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileDTO updateProfileDTO) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.getUser(token);

        return userService.updateUserProfile(email, updateProfileDTO)
                .map(user -> ResponseEntity.ok(UserMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change user password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordDTO changePasswordDTO) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.getUser(token);

        boolean success = userService.changePassword(email, changePasswordDTO);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

}

