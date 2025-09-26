package com.ms.LoginAuthetication.service;

import com.ms.LoginAuthetication.dto.request.LoginRequest;
import com.ms.LoginAuthetication.dto.request.RegisterRequest;
import com.ms.LoginAuthetication.dto.request.UpdateRequest;
import com.ms.LoginAuthetication.dto.response.LoginResponse;
import com.ms.LoginAuthetication.dto.response.RegisterUserResponse;
import com.ms.LoginAuthetication.dto.response.UserResponse;
import com.ms.LoginAuthetication.model.User;
import com.ms.LoginAuthetication.repository.UserRepository;
import com.ms.LoginAuthetication.security.jwt.JwtUtils;
import com.ms.LoginAuthetication.security.services.UserDetailsImpl;
import com.ms.LoginAuthetication.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.ms.LoginAuthetication.model.ERole.ROLE_USER;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final ConfirmationKeyService confirmationKeyService;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder,
                       AuthenticationManager authenticationManager, JwtUtils jwtUtils, EmailService emailService, ConfirmationKeyService confirmationKeyService){
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.confirmationKeyService = confirmationKeyService;
    }



    @Transactional
    public Response<Object> register(RegisterRequest request){

        usernameExists(request.getUsername());
        emailExists(request.getEmail());

        String hashedPassword = encoder.encode(request.getPassword());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setRoles(new HashSet<>(Set.of(ROLE_USER.name())));
        user.setIsActive(false);

        userRepository.save(user);
        String key = confirmationKeyService.createKey(user);
        emailService.sendVerificationEmail(request.getEmail(), key);

        RegisterUserResponse registerUserResponse = RegisterUserResponse.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .build();

        return Response.builder()
                .responseCode(200)
                .responseMessage("SUCCESS")
                .data(registerUserResponse)
                .build();
    }

    @Transactional
    public Response<Object> login(LoginRequest request){

        userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User non trovato"));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        LoginResponse loginResponse = LoginResponse.builder()
                .username(userDetails.getUsername())
                .email(userDetails.getPassword())
                .roles(roles)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();

        return Response.builder()
                .responseCode(200)
                .responseMessage("SUCCESS")
                .data(loginResponse)
                .build();
    }

    @Transactional
    public Response<Object> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email non trovata"));
        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .roles(user.getRoles())
                .build();

        return Response.builder()
                .responseCode(200)
                .responseMessage("SUCCESS")
                .data(userResponse)
                .build();
    }

    @Transactional
    public Response<Object> updateUser(UpdateRequest updateRequest){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("UPDATE ROLES - SERVICE - ADMIN: {}", userDetails.getUsername());
        User user = userRepository.findById(updateRequest.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        if(updateRequest.getUsername() != null){
            String username = updateRequest.getUsername();
            if(!user.getUsername().equals(username)) usernameExists(username);
            user.setUsername(username);
        }

        if(updateRequest.getEmail() != null){
            String email = updateRequest.getEmail();
            if(!user.getEmail().equals(email)) emailExists(email);
            user.setEmail(email);
        }

        if(updateRequest.getPassword() != null){
            String hashedPassword = encoder.encode(updateRequest.getPassword());
            user.setPassword(hashedPassword);
        }

        if(updateRequest.getIsActive() != null){
            user.setIsActive(updateRequest.getIsActive());
        }

        if(updateRequest.getRoles() != null && !updateRequest.getRoles().isEmpty()){
            user.setRoles(updateRequest.getRoles());
        }

        userRepository.save(user);

        return Response.builder()
                .responseCode(200)
                .responseMessage("SUCCESS")
                .data(updateRequest)
                .build();
    }


    private void usernameExists(String username){
        if(Boolean.TRUE.equals(userRepository.existsByUsername(username))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username già registrato");
        }
    }
    private void emailExists(String email){
        if(Boolean.TRUE.equals(userRepository.existsByEmail(email))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email già registrata");
        }
    }
}
