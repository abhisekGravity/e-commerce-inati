package com.example.commerce.cart.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private String productId;
    private String sku;
    private String name;

    private int quantity;

    /** Final price after dynamic pricing */
    private BigDecimal unitPrice;

    /** unitPrice × quantity */
    private BigDecimal totalPrice;

    public void recalculate() {
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
