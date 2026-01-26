package com.example.ecommerce.exception.tenant;

import org.springframework.http.HttpStatus;

public abstract class TenantException extends RuntimeException {

    private final HttpStatus status;

    protected TenantException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}