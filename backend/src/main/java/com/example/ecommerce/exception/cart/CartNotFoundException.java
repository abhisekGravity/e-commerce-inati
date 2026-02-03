package com.example.ecommerce.exception.cart;

import org.springframework.http.HttpStatus;

public class CartNotFoundException extends CartException {

    public CartNotFoundException() {
        super("No cart found", HttpStatus.NOT_FOUND);
    }
}
