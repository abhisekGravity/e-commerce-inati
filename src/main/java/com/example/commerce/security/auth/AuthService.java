package com.example.commerce.security.auth;

import com.example.commerce.User.User;
import com.example.commerce.User.UserRepository;
import com.example.commerce.exception.UserAlreadyExistsException;
import com.example.commerce.security.auth.dto.AuthRequest;
import com.example.commerce.security.jwt.JwtService;
import com.example.commerce.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public String register(AuthRequest request) {
        String currentTenantId = TenantContext.getTenantId();

        if (currentTenantId == null) {
            throw new IllegalStateException("Tenant Context is missing.");
        }

        if (userRepository.existsByTenantIdAndEmail(currentTenantId, request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use for this tenant");
        }

        User user = User.builder()
                .tenantId(currentTenantId)
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return jwtService.generateAccessToken(
                user.getId(), user.getTenantId(), user.getTokenVersion());
    }

    public AuthTokens login(AuthRequest request) {
        String tenantId = TenantContext.getTenantId();

        User user = userRepository
                .findByTenantIdAndEmail(tenantId, request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        refreshTokenRepository.deleteAllByUserId(user.getId());

        return issueNewTokenPair(user);
    }

    public AuthTokens refresh(String rawRefreshToken) {

        String hash = hash(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow();

        if (stored.getTokenVersion() != user.getTokenVersion()) {
            throw new RuntimeException("Refresh token invalidated");
        }

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getTenantId(),
                user.getTokenVersion()
        );

        return new AuthTokens(newAccessToken, rawRefreshToken);
    }

    public void logout(String userId, String tenantId) {

        User user = userRepository
                .findByIdAndTenantId(userId, tenantId)
                .orElseThrow();

        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        refreshTokenRepository.deleteAllByUserId(user.getId());
    }

    private AuthTokens issueNewTokenPair(User user) {

        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getTenantId(), user.getTokenVersion());

        String refreshToken = Base64.getUrlEncoder().encodeToString(
                (user.getId() + ":" + System.nanoTime()).getBytes()
        );

        RefreshToken entity = new RefreshToken();
        entity.setUserId(user.getId());
        entity.setTenantId(user.getTenantId());
        entity.setTokenHash(hash(refreshToken));
        entity.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 3600));
        entity.setTokenVersion(user.getTokenVersion());
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
