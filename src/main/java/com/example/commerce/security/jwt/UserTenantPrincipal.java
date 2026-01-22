package com.example.commerce.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTenantPrincipal {
    private final String userId;
    private final String tenantId;
}
