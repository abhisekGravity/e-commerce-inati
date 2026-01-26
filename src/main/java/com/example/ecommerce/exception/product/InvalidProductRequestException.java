package com.example.ecommerce.exception.product;

import org.springframework.http.HttpStatus;

public class InvalidProductRequestException extends ProductException {

    public InvalidProductRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
