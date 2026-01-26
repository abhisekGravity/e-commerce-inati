package com.example.commerce.pricing.strategies;

import com.example.commerce.pricing.engine.PricingStrategy;
import com.example.commerce.pricing.dto.PricingContext;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class PercentageDiscountStrategy implements PricingStrategy {

    private final BigDecimal discountPercentage;

    @Override
    public boolean isApplicable(PricingContext context) {
        return discountPercentage.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal apply(PricingContext context) {
        return context.basePrice()
                .subtract(context.basePrice().multiply(discountPercentage));
    }
}
