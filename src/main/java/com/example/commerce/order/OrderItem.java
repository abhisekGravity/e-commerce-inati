package com.example.commerce.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {

    private String productId;
    private int quantity;
    private BigDecimal price;
}
