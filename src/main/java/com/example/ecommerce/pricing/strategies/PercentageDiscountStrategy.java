package com.example.ecommerce.pricing.strategies;

import com.example.ecommerce.pricing.engine.PricingStrategy;
import com.example.ecommerce.pricing.dto.PricingContext;
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
