package com.example.ecommerce.auth.controller;

import com.example.ecommerce.auth.service.AuthService;
import com.example.ecommerce.auth.domain.AuthTokens;
import com.example.ecommerce.auth.dto.AuthRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthTokens register(@Valid @RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthTokens login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthTokens refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody(required = false) String refreshToken) {
        if (refreshToken != null) {
            authService.logoutByRefreshToken(refreshToken);
        }

        return ResponseEntity.ok("User logged out successfully");
    }
}