package com.example.commerce.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {

    private List<CartItemResponse> items;
    private BigDecimal total;
}
