package com.example.commerce.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    private String id;
    private String sku;
    private String name;
    private int inventory;
    private BigDecimal price;

    public ProductResponse(Product product, BigDecimal price) {
        this.id = product.getId();
        this.sku = product.getSku();
        this.name = product.getName();
        this.inventory = product.getInventory();
        this.price = price;
    }
}
