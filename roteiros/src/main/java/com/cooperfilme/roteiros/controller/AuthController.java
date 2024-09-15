package com.cooperfilme.roteiros.controller;

import com.cooperfilme.roteiros.dto.LoginRequest;
import com.cooperfilme.roteiros.dto.LoginResponse;
import com.cooperfilme.roteiros.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
