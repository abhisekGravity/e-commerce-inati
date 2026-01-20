package com.example.commerce.security.auth;

import com.example.commerce.security.auth.dto.AuthRequest;
import com.example.commerce.security.auth.dto.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
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
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response) {

        AuthTokens tokens = authService.refresh(refreshToken);
        setRefreshCookie(response, tokens.getRefreshToken());
        return new AuthResponse(tokens.getAccessToken());
    }

    @PostMapping("/logout")
    public void logout(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response) {

        authService.logout(refreshToken);

        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void setRefreshCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 3600);
        response.addCookie(cookie);
    }
}
