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
    public void logout(@AuthenticationPrincipal UserTenantPrincipal principal,
                       HttpServletResponse response) {

        if (principal != null) {
            authService.logout(principal.getUserId(), principal.getTenantId());
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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