package com.example.commerce.pricing;

import com.example.commerce.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FlatDiscountRule implements PricingRule {

    @Override
    public boolean supports(Product product) {
        return product.getBasePrice().compareTo(BigDecimal.valueOf(1000)) > 0;
    }

    @Override
    public BigDecimal apply(Product product, BigDecimal currentPrice) {
        return currentPrice.subtract(BigDecimal.valueOf(100));
    }
}
