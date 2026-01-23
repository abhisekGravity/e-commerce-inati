package com.example.commerce.exception.tenant;

import org.springframework.http.HttpStatus;

public class TenantNotFoundException extends TenantException {

    public TenantNotFoundException(String tenantId) {
        super("Tenant with ID '" + tenantId + "' not found.", HttpStatus.NOT_FOUND);
    }
}