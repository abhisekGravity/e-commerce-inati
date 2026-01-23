package com.example.commerce.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {

    private String id;
    private String sku;
    private String name;
    private int inventory;
    private BigDecimal price;
}
