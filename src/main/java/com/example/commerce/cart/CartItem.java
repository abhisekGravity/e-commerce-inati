package com.example.commerce.cart;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItem {
    private String productId;
    private int quantity;
    private BigDecimal price;
}
