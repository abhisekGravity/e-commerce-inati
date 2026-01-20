package com.example.commerce.pricing;

import com.example.commerce.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InventoryPricingRule implements PricingRule {

    @Override
    public boolean supports(Product product) {
        return product.getInventory() < 5;
    }

    @Override
    public BigDecimal apply(Product product, BigDecimal currentPrice) {
        return currentPrice.multiply(BigDecimal.valueOf(1.1));
    }
}
