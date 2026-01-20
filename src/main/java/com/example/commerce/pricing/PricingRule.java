package com.example.commerce.pricing;

import com.example.commerce.product.Product;

import java.math.BigDecimal;

public interface PricingRule {

    boolean supports(Product product);

    BigDecimal apply(Product product, BigDecimal currentPrice);
}
