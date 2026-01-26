package com.example.ecommerce.exception.order;

import org.springframework.http.HttpStatus;

public class EmptyCartException extends OrderException {
    public EmptyCartException() {
        super("Cart is empty", HttpStatus.BAD_REQUEST);
    }
}