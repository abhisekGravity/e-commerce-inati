package com.example.ecommerce.pricing.strategies;

import com.example.ecommerce.pricing.engine.PricingStrategy;
import com.example.ecommerce.pricing.dto.PricingContext;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class FlatDiscountStrategy implements PricingStrategy {

    private final BigDecimal discountAmount;

    @Override
    public boolean isApplicable(PricingContext context) {
        return discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal apply(PricingContext context) {
        BigDecimal discounted = context.basePrice().subtract(discountAmount);
        return discounted.compareTo(BigDecimal.ZERO) > 0 ? discounted : BigDecimal.ZERO;
    }
}
