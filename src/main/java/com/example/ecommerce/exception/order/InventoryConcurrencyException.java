package com.example.ecommerce.exception.order;

import org.springframework.http.HttpStatus;

public class InventoryConcurrencyException extends OrderException {
    public InventoryConcurrencyException(String sku) {
        super("Concurrent inventory update detected for SKU: " + sku, HttpStatus.CONFLICT);
    }
}