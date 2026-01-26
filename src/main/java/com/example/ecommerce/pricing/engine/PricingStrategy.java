package com.example.ecommerce.pricing.engine;

import com.example.ecommerce.pricing.dto.PricingContext;
import java.math.BigDecimal;

public interface PricingStrategy {
    boolean isApplicable(PricingContext context);
    BigDecimal apply(PricingContext context);
}
