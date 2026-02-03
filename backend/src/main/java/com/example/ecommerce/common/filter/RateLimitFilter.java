package com.example.ecommerce.common.filter;

import com.example.ecommerce.common.rateLimit.RateLimitService;
import com.example.ecommerce.common.response.ApiErrorResponse;
import com.example.ecommerce.security.util.SecurityUtil;
import com.example.ecommerce.tenant.context.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String tenantId = TenantContext.getTenantId();
        String clientIp = request.getRemoteAddr();
        String apiPath = request.getRequestURI();
        String userId = SecurityUtil.getCurrentUserId();

        boolean allowed = rateLimitService.allowRequest(tenantId, clientIp, userId, apiPath);

        if (!allowed) {
            ApiErrorResponse errorResponse = new ApiErrorResponse(
                    "RATE_LIMIT_EXCEEDED",
                    "Rate limit exceeded",
                    Instant.now().toEpochMilli()
                    );

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }
}