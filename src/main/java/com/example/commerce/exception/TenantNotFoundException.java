package com.example.commerce.exception;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String tenantId) {
        super("Tenant with ID '" + tenantId + "' not found.");
    }
}