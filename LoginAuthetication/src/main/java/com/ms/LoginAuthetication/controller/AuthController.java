package com.ms.LoginAuthetication.controller;

import com.ms.LoginAuthetication.dto.request.LoginRequest;
import com.ms.LoginAuthetication.dto.request.RegisterRequest;
import com.ms.LoginAuthetication.dto.request.UpdateRequest;
import com.ms.LoginAuthetication.service.AuthService;
import com.ms.LoginAuthetication.service.ConfirmationKeyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final ConfirmationKeyService confirmationService;


    public AuthController(AuthService authService, ConfirmationKeyService confirmationService){
        this.authService = authService;
        this.confirmationService = confirmationService;
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("keyConfirm") String keyConfirm){
        boolean verified = confirmationService.verifyKey(keyConfirm);
        if(verified){
            return ResponseEntity.ok("Email confermata con successo! Ora puoi effettuare il login.");
        }else {
            return ResponseEntity.badRequest().body("Autenticazione non valida o scaduta");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getUser(){
        return ResponseEntity.ok(authService.getUser());
    }

    @PutMapping("/updateUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateRequest updateRequest){
        return ResponseEntity.ok(authService.updateUser(updateRequest));
    }
}
