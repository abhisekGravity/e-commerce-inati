package com.example.ecommerce.auth.service;

import com.example.ecommerce.User.domain.User;
import com.example.ecommerce.User.repository.UserRepository;
import com.example.ecommerce.exception.auth.InvalidRefreshTokenException;
import com.example.ecommerce.exception.auth.RefreshTokenExpiredException;
import com.example.ecommerce.exception.auth.UserAlreadyExistsException;
import com.example.ecommerce.exception.auth.UserNotFoundException;
import com.example.ecommerce.auth.domain.AuthTokens;
import com.example.ecommerce.auth.domain.RefreshToken;
import com.example.ecommerce.auth.repository.RefreshTokenRepository;
import com.example.ecommerce.auth.dto.AuthRequest;
import com.example.ecommerce.security.jwt.JwtService;
import com.example.ecommerce.tenant.context.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
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
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
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
                .findByTokenHash(hash)
                .orElseThrow(() -> new InvalidRefreshTokenException());

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenExpiredException();
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow();

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getTenantId(),
                user.getTokenVersion()
        );

        return new AuthTokens(newAccessToken, rawRefreshToken);
    }

    @Transactional
    public void logoutByRefreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository
                .findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid refresh token"
                ));

        refreshTokenRepository.delete(token);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserNotFoundException());

        user.setTokenVersion(0);
        userRepository.save(user);

    }

    private AuthTokens issueNewTokenPair(User user) {

        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getTenantId(), user.getTokenVersion());

        String refreshToken =generateRefreshToken();

        RefreshToken entity = RefreshToken.builder()
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .tokenHash(hash(refreshToken))
                .expiresAt(Instant.now().plusSeconds(7 * 24 * 3600))
                .build();

        refreshTokenRepository.save(entity);

        return new AuthTokens(accessToken, refreshToken);
    }

    private String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
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
