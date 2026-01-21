package com.example.commerce.exception;

public class TenantAlreadyExistsException extends RuntimeException {
    public TenantAlreadyExistsException(String name) {
        super("Tenant with name '" + name + "' already exists.");
    }
}