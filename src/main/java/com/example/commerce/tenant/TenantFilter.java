package com.example.commerce.tenant;

import com.example.commerce.exception.tenant.TenantNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantRepository tenantRepository;

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/tenants",
            "/error"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String tenantSlug = request.getHeader("x-tenant-slug");

        boolean isPublicEndpoint =
                PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);

        if (!isPublicEndpoint && (tenantSlug == null || tenantSlug.isBlank())) {
            response.sendError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Missing x-tenant-slug header"
            );
            return;
        }

        try {
            if (tenantSlug != null && !tenantSlug.isBlank()) {
                Tenant tenant = tenantRepository.findByTenantSlug(tenantSlug)
                        .orElseThrow(() -> new TenantNotFoundException(tenantSlug));

                TenantContext.setTenantId(tenant.getId());
            }

            filterChain.doFilter(request, response);

        } catch (TenantNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Tenant not found for slug: " + tenantSlug + "\"}"
            );
        } finally {
            TenantContext.clear();
        }
    }
}