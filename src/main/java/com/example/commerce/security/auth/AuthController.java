package com.example.commerce.security.auth;

import com.example.commerce.security.auth.dto.AuthRequest;
import com.example.commerce.security.auth.dto.AuthResponse;
import com.example.commerce.security.jwt.UserTenantPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody AuthRequest request,
            HttpServletResponse response) {

        String accessToken = authService.register(request);
        return new AuthResponse(accessToken);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody AuthRequest request,
            HttpServletResponse response) {

        AuthTokens tokens = authService.login(request);
        setRefreshCookie(response, tokens.getRefreshToken());
        return new AuthResponse(tokens.getAccessToken());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        AuthTokens tokens = authService.refresh(refreshToken);
        setRefreshCookie(response, tokens.getRefreshToken());
        return new AuthResponse(tokens.getAccessToken());
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal UserTenantPrincipal principal,
            HttpServletResponse response) {

        if (principal != null) {
            authService.logout(principal.getUserId(), principal.getTenantId());
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 3600)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}