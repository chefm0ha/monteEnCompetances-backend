package com.competencesplateforme.authservice.dto;

import com.competencesplateforme.authservice.model.User;

import java.util.Optional;

public class LoginResponseDTO {
    private final String token;
    private final UserDTO userDTO ;

    public LoginResponseDTO(UserDTO userDTO ,String token) {
        this.token = token;
        this.userDTO = userDTO;
    }

    public String getToken() {
        return token;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

}
