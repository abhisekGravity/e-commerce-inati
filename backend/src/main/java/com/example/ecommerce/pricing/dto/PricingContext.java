package com.example.ecommerce.pricing.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PricingContext(
        String tenantId,
        String sku,
        BigDecimal basePrice,
        int inventory
) {}
