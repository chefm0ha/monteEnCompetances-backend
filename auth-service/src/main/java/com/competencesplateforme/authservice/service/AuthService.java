package com.competencesplateforme.authservice.service;

import com.competencesplateforme.authservice.dto.LoginRequestDTO;
import com.competencesplateforme.authservice.dto.UserDTO;
import com.competencesplateforme.authservice.mapper.UserMapper;
import com.competencesplateforme.authservice.model.LogoutToken;
import com.competencesplateforme.authservice.model.User;
import com.competencesplateforme.authservice.repository.LogoutTokenRepository;
import com.competencesplateforme.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LogoutTokenRepository logoutTokenRepository;

    public AuthService(UserService userService , PasswordEncoder passwordEncoder , JwtUtil jwtUtil , LogoutTokenRepository logoutTokenRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.logoutTokenRepository = logoutTokenRepository ;
    }

    public Map<String, Object> authenticate(LoginRequestDTO loginRequestDTO) {
        Map<String, Object > loginMap = new HashMap<>();

        Optional<User> userOptional = userService.findByEmail(loginRequestDTO.getEmail());


        if (userOptional.isEmpty()) {
            loginMap.put("user", null);
            loginMap.put("token", null);
            return loginMap;
        }

        User user = userOptional.get();

        UserDTO userDTO = UserMapper.toDto(user);

        loginMap.put("user", userDTO);

        Optional<String> token = Optional.empty();
        if (passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            token = Optional.of(jwtUtil.generateToken(user.getEmail(), user.getRole()));
        }

        loginMap.put("token", token.orElse(null));

        return loginMap ;
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }

    public boolean logoutToken(String token) {
        try {
            jwtUtil.validateToken(token) ;

            LogoutToken logoutToken = new LogoutToken();
            logoutToken.setToken(token);
            logoutToken.setLogoutDate(LocalDate.now());

            logoutTokenRepository.save(logoutToken);

            return true;

        }catch (JwtException e){

            return false;
        }
    }

    public boolean isAdmin(String token){
        String email = jwtUtil.getUser(token) ;
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getRole().equals("ADMIN");
        }else {
            return false;
        }
    }

    public boolean isCollaborator(String token){
        String email = jwtUtil.getUser(token) ;
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getRole().equals("COLLABORATEUR");
        }else {
            return false;
        }

    }


}
