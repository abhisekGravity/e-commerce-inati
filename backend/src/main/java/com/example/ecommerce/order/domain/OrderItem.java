package com.example.ecommerce.order.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String productId;
    private String sku;
    private String name;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal baseUnitPrice;

    private BigDecimal totalPrice;
}
