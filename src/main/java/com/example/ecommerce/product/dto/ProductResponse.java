package com.example.ecommerce.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {

    private String id;
    private String sku;
    private String name;
    private BigDecimal price;
    private int inventory;
}
