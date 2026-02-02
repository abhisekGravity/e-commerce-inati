package com.example.ecommerce.exception.product;

import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends ProductException {

    public ProductAlreadyExistsException(String sku, String tenantId) {
        super("SKU '" + sku + "' already exists for tenant with ID '" + tenantId + "'", HttpStatus.CONFLICT);
    }
}
