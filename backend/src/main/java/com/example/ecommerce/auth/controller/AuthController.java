package com.example.ecommerce.auth.controller;

import com.example.ecommerce.auth.service.AuthService;
import com.example.ecommerce.auth.domain.AuthTokens;
import com.example.ecommerce.auth.dto.AuthRequest;
import com.example.ecommerce.auth.dto.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest request) {
        return new AuthResponse(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request,
                              HttpServletResponse response) {

        AuthTokens tokens = authService.login(request);
        setRefreshCookie(response, tokens.getRefreshToken());
        return new AuthResponse(tokens.getAccessToken());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(
            @CookieValue(name = "refresh_token") String refreshToken,
            HttpServletResponse response) {

        AuthTokens tokens = authService.refresh(refreshToken);
        setRefreshCookie(response, tokens.getRefreshToken());
        return new AuthResponse(tokens.getAccessToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            authService.logoutByRefreshToken(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                ("User logged out successfully")
        );
    }

    private void setRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 3600)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}