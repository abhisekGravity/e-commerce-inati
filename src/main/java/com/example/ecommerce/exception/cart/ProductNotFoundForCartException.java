package com.example.ecommerce.exception.cart;

import org.springframework.http.HttpStatus;

public class ProductNotFoundForCartException extends CartException {

    public ProductNotFoundForCartException(String sku) {
        super("Product not found for SKU: " + sku, HttpStatus.NOT_FOUND);
    }
}
