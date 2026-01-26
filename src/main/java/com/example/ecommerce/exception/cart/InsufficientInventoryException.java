package com.example.ecommerce.exception.cart;

import org.springframework.http.HttpStatus;

public class InsufficientInventoryException extends CartException {

    public InsufficientInventoryException(int requested, int available) {
        super(
            "Insufficient inventory. Requested: " + requested + ", Available: " + available,
            HttpStatus.BAD_REQUEST
        );
    }
}
