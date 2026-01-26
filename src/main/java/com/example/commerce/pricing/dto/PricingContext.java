package com.example.commerce.pricing.dto;

import java.math.BigDecimal;

public record PricingContext(
        String tenantId,
        String sku,
        BigDecimal basePrice,
        int inventory
) {}
