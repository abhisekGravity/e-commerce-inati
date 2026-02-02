package com.example.ecommerce.exception.order;

import org.springframework.http.HttpStatus;

public class InsufficientInventoryForOrderException extends OrderException {
    public InsufficientInventoryForOrderException(String sku) {
        super("Insufficient inventory for SKU: " + sku, HttpStatus.BAD_REQUEST);
    }
}