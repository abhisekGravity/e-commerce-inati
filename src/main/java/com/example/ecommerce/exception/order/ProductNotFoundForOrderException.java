package com.example.ecommerce.exception.order;

import org.springframework.http.HttpStatus;

public class ProductNotFoundForOrderException extends OrderException {
    public ProductNotFoundForOrderException(String sku) {
        super("Product not found for SKU: " + sku, HttpStatus.NOT_FOUND);
    }
}