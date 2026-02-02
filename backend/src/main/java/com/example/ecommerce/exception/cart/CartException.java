package com.example.ecommerce.exception.cart;

import org.springframework.http.HttpStatus;

public abstract class CartException extends RuntimeException {

    private final HttpStatus status;

    protected CartException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}