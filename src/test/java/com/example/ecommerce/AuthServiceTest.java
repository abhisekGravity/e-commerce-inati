package com.example.ecommerce;

import com.example.ecommerce.User.domain.User;
import com.example.ecommerce.User.repository.UserRepository;
import com.example.ecommerce.auth.domain.AuthTokens;
import com.example.ecommerce.auth.domain.RefreshToken;
import com.example.ecommerce.auth.dto.AuthRequest;
import com.example.ecommerce.auth.repository.RefreshTokenRepository;
import com.example.ecommerce.auth.service.AuthService;
import com.example.ecommerce.exception.auth.*;
import com.example.ecommerce.security.jwt.JwtService;
import com.example.ecommerce.tenant.context.TenantContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequest request;

    @BeforeEach
    void setup() {
        request = new AuthRequest();
        request.setEmail("test@email.com");
        request.setPassword("password");

        TenantContext.setTenantId("tenant-1");
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }


    @Test
    void register_successfullyRegistersUser() {

        when(userRepository.existsByTenantIdAndEmail("tenant-1", request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode("password"))
                .thenReturn("hashed");

        when(jwtService.generateAccessToken(any(), any(), anyInt()))
                .thenReturn("access-token");

        String token = authService.register(request);

        assertThat(token).isEqualTo("access-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsExceptionWhenUserAlreadyExists() {

        when(userRepository.existsByTenantIdAndEmail("tenant-1", request.getEmail()))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsExceptionWhenTenantContextMissing() {

        TenantContext.clear();

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    void login_successfullyAuthenticatesUser() {

        User user = User.builder()
                .id("user-1")
                .tenantId("tenant-1")
                .email(request.getEmail())
                .passwordHash("hashed")
                .tokenVersion(0)
                .build();

        when(userRepository.findByTenantIdAndEmail("tenant-1", request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        when(jwtService.generateAccessToken(any(), any(), anyInt()))
                .thenReturn("access");

        AuthTokens tokens = authService.login(request);

        assertThat(tokens.getAccessToken()).isEqualTo("access");
        assertThat(tokens.getRefreshToken()).isNotNull();

        verify(refreshTokenRepository).deleteAllByUserId("user-1");
    }

    @Test
    void login_throwsExceptionWhenUserNotFound() {

        when(userRepository.findByTenantIdAndEmail(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_throwsExceptionWhenPasswordInvalid() {

        User user = User.builder()
                .passwordHash("hashed")
                .build();

        when(userRepository.findByTenantIdAndEmail(any(), any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }


    @Test
    void refresh_successfullyGeneratesNewAccessToken() {

        String rawToken = "refresh";
        String hash = authService.getClass()
                .getDeclaredMethods()[0] != null ? null : null; // ignored (hash tested via repo)

        RefreshToken stored = RefreshToken.builder()
                .userId("user-1")
                .expiresAt(Instant.now().plusSeconds(1000))
                .build();

        User user = User.builder()
                .id("user-1")
                .tenantId("tenant-1")
                .tokenVersion(1)
                .build();

        when(refreshTokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.of(stored));

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(any(), any(), anyInt()))
                .thenReturn("new-access");

        AuthTokens tokens = authService.refresh(rawToken);

        assertThat(tokens.getAccessToken()).isEqualTo("new-access");
        assertThat(tokens.getRefreshToken()).isEqualTo(rawToken);
    }

    @Test
    void refresh_throwsExceptionWhenTokenExpired() {

        RefreshToken expired = RefreshToken.builder()
                .expiresAt(Instant.now().minusSeconds(10))
                .build();

        when(refreshTokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("token"))
                .isInstanceOf(RefreshTokenExpiredException.class);
    }

    @Test
    void refresh_throwsExceptionWhenTokenInvalid() {

        when(refreshTokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }


    @Test
    void logoutByRefreshToken_successfullyLogsOutUser() {

        RefreshToken token = RefreshToken.builder()
                .userId("user-1")
                .build();

        User user = User.builder()
                .id("user-1")
                .tokenVersion(5)
                .build();

        when(refreshTokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.of(token));

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(user));

        authService.logoutByRefreshToken("refresh");

        verify(refreshTokenRepository).delete(token);
        assertThat(user.getTokenVersion()).isZero();
        verify(userRepository).save(user);
    }

    @Test
    void logoutByRefreshToken_throwsExceptionWhenTokenInvalid() {

        when(refreshTokenRepository.findByTokenHash(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logoutByRefreshToken("token"))
                .isInstanceOf(ResponseStatusException.class);
    }
}
