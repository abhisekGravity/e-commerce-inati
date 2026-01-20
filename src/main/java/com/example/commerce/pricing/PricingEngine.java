package com.example.commerce.pricing;

import com.example.commerce.product.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class PricingEngine {

    private final List<PricingRule> rules;

    public BigDecimal calculatePrice(Product product) {
        BigDecimal price = product.getBasePrice();

        for (PricingRule rule : rules) {
            if (rule.supports(product)) {
                price = rule.apply(product, price);
            }
        }
        return price.max(BigDecimal.ZERO);
    }
}
