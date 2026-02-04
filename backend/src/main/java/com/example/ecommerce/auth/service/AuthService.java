package com.example.ecommerce.auth.service;

import com.example.ecommerce.User.domain.User;
import com.example.ecommerce.User.repository.UserRepository;
import com.example.ecommerce.exception.auth.InvalidRefreshTokenException;
import com.example.ecommerce.exception.auth.UserAlreadyExistsException;
import com.example.ecommerce.exception.auth.UserNotFoundException;
import com.example.ecommerce.auth.domain.AuthTokens;
import com.example.ecommerce.auth.dto.AuthRequest;
import com.example.ecommerce.security.jwt.JwtService;
import com.example.ecommerce.tenant.context.TenantContext;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthTokens register(AuthRequest request) {
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

        return issueNewTokenPair(user);
    }

    public AuthTokens login(AuthRequest request) {
        String tenantId = TenantContext.getTenantId();

        User user = userRepository
                .findByTenantIdAndEmail(tenantId, request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return issueNewTokenPair(user);
    }

    public AuthTokens refresh(String rawRefreshToken) {
        try {
            Claims claims = jwtService.validateToken(rawRefreshToken);
            String userId = claims.getSubject();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException());

            String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getTenantId());
            return new AuthTokens(newAccessToken, rawRefreshToken);
        } catch (Exception e) {
            throw new InvalidRefreshTokenException();
        }
    }

    @Transactional
    public void logoutByRefreshToken(String refreshToken) {
    }

    private AuthTokens issueNewTokenPair(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getTenantId());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getTenantId());
        return new AuthTokens(accessToken, refreshToken);
    }
}
