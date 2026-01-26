package com.example.commerce.pricing.engine;

import com.example.commerce.pricing.dto.PricingContext;
import java.math.BigDecimal;

public interface PricingStrategy {
    boolean isApplicable(PricingContext context);
    BigDecimal apply(PricingContext context);
}
