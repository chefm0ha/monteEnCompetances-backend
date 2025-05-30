package com.competencesplateforme.authservice.mapper;


import com.competencesplateforme.authservice.dto.UserDTO;
import com.competencesplateforme.authservice.model.User;

import java.util.Optional;

public class UserMapper {

    public static UserDTO toDto(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}
