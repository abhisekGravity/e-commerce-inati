package com.example.commerce.exception.dscountRule;

import org.springframework.http.HttpStatus;

public abstract class DiscountRuleException extends RuntimeException {

    private final HttpStatus status;

    protected DiscountRuleException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}