package com.example.ecommerce.exception.cart;

import org.springframework.http.HttpStatus;

public class ActiveCartNotFoundException extends CartException {

    public ActiveCartNotFoundException() {
        super("No active cart found", HttpStatus.NOT_FOUND);
    }
}
