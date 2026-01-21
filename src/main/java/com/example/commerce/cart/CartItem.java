package com.example.commerce.cart;

import lombok.Data;

@Data
public class CartItem {
    private String productId;
    private int quantity;
}
