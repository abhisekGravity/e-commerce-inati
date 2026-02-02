package com.example.ecommerce.exception.tenant;

import org.springframework.http.HttpStatus;

public class TenantNotFoundException extends TenantException {

    public TenantNotFoundException(String tenantId) {
        super("Tenant with ID '" + tenantId + "' not found.", HttpStatus.NOT_FOUND);
    }
}