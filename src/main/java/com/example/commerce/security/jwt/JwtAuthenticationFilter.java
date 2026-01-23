package com.example.commerce.security.jwt;

import com.example.commerce.User.User;
import com.example.commerce.User.UserRepository;
import com.example.commerce.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                reject(response);
                return;
            }

            String token = authHeader.substring(7);
            Claims claims = jwtService.validateToken(token);

            String userId = claims.getSubject();
            String tenantId = claims.get("tenantId", String.class);
            Integer tokenVersion = claims.get("tokenVersion", Integer.class);

            String contextTenantId = TenantContext.getTenantId();
            if (contextTenantId != null && !contextTenantId.equals(tenantId)) {
                reject(response);
                return;
            }

            User user = userRepository.findById(userId)
                    .orElseThrow();

            if (!tokenVersion.equals(user.getTokenVersion())) {
                reject(response);
                return;
            }

            UserTenantPrincipal principal =
                    new UserTenantPrincipal(userId, tenantId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            reject(response);
        }
    }

    private void reject(HttpServletResponse response) throws IOException {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Unauthorized\"}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/auth/")
                || path.startsWith("/tenants/")
                || path.equals("/error");
    }
}