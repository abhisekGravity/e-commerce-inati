package com.example.ecommerce.exception.order;

import org.springframework.http.HttpStatus;

public class ActiveCartNotFoundForOrderException extends OrderException {
    public ActiveCartNotFoundForOrderException() {
        super("No active cart found for order", HttpStatus.NOT_FOUND);
    }
}