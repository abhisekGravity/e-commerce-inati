package com.example.ecommerce.exception.tenant;

import org.springframework.http.HttpStatus;

public class TenantAlreadyExistsException extends TenantException {

    public TenantAlreadyExistsException(String name) {
        super("Tenant with name '" + name + "' already exists.", HttpStatus.CONFLICT);
    }
}