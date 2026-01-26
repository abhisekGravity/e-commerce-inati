package com.example.ecommerce.pricing.engine;

import com.example.ecommerce.pricing.dto.PricingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PricingEngine {

    private final List<PricingStrategy> strategies;

    public BigDecimal calculatePrice(PricingContext context) {
        BigDecimal price = context.basePrice();

        for (PricingStrategy strategy : strategies) {
            if (strategy.isApplicable(context)) {
                price = strategy.apply(new PricingContext(
                        context.tenantId(),
                        context.sku(),
                        price,
                        context.inventory()
                ));
            }
        }

        return price;
    }
}