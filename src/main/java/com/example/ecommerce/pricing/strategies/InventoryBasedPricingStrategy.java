package com.example.ecommerce.pricing.strategies;

import com.example.ecommerce.pricing.engine.PricingStrategy;
import com.example.ecommerce.pricing.dto.PricingContext;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class InventoryBasedPricingStrategy implements PricingStrategy {

    private final int threshold;
    private final BigDecimal discount;

    @Override
    public boolean isApplicable(PricingContext context) {
        return context.inventory() < threshold;
    }

    @Override
    public BigDecimal apply(PricingContext context) {
        BigDecimal discounted = context.basePrice().subtract(discount);
        return discounted.compareTo(BigDecimal.ZERO) > 0 ? discounted : BigDecimal.ZERO;
    }
}
