package com.example.commerce.security.auth;

import com.example.commerce.User.User;
import com.example.commerce.User.UserRepository;
import com.example.commerce.security.auth.dto.AuthRequest;
import com.example.commerce.security.jwt.JwtService;
import com.example.commerce.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String register(AuthRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(encoder.encode(request.getPassword()));
        user.setTenant();

        userRepository.save(user);

        return jwtService.generateAccessToken(user.getId(), user.getTenantId());
    }

    public AuthTokens login(AuthRequest request) {
        User user = userRepository
                .findByTenantIdAndEmail(TenantContext.getTenantId(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return issueTokens(user);
    }

    public AuthTokens refresh(String rawRefreshToken) {
        String hash = hash(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow();

        return issueTokens(user);
    }

    public void logout(String rawRefreshToken) {
        String hash = hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthTokens issueTokens(User user) {
        String accessToken =
                jwtService.generateAccessToken(user.getId(), user.getTenantId());

        String refreshToken = Base64.getUrlEncoder().encodeToString(
                (user.getId() + ":" + System.nanoTime()).getBytes()
        );

        RefreshToken entity = new RefreshToken();
        entity.setUserId(user.getId());
        entity.setTenantId(user.getTenantId());
        entity.setTokenHash(hash(refreshToken));
        entity.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 3600));
        entity.setRevoked(false);

        refreshTokenRepository.save(entity);

        return new AuthTokens(accessToken, refreshToken);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(
                    digest.digest(value.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
