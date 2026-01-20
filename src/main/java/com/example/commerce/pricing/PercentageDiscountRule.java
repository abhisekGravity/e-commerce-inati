package com.example.commerce.pricing;

import com.example.commerce.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PercentageDiscountRule implements PricingRule {

    @Override
    public boolean supports(Product product) {
        return product.getInventory() > 10;
    }

    @Override
    public BigDecimal apply(Product product, BigDecimal currentPrice) {
        return currentPrice.multiply(BigDecimal.valueOf(0.9));
    }
}
