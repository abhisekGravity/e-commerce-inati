package com.example.commerce.pricing.service;

import com.example.commerce.pricing.dto.PricingContext;
import com.example.commerce.pricing.engine.PricingEngine;
import com.example.commerce.pricing.strategies.FlatDiscountStrategy;
import com.example.commerce.pricing.strategies.InventoryBasedPricingStrategy;
import com.example.commerce.pricing.strategies.PercentageDiscountStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Production-level PricingService
 * - Multi-tenant aware
 * - Builds PricingEngine with all strategies
 * - Calculates final price for products
 */
@Service
public class PricingService {

    /**
     * For simplicity, hardcoding rules here.
     * In real prod, fetch per tenant from DB/config.
     */
    public BigDecimal getFinalPrice(String tenantId, String sku, BigDecimal basePrice, int inventory) {
        List strategies = new ArrayList<>();

        // Example: hardcoded per-tenant strategies
        // Could load from DB for dynamic rules per tenant
        strategies.add(new PercentageDiscountStrategy(BigDecimal.valueOf(0.10))); // 10% off
        strategies.add(new FlatDiscountStrategy(BigDecimal.valueOf(5))); // $5 off
        strategies.add(new InventoryBasedPricingStrategy(10, BigDecimal.valueOf(2))); // low stock discount

        PricingEngine engine = new PricingEngine(strategies);

        PricingContext context = new PricingContext(tenantId, sku, basePrice, inventory);
        return engine.calculatePrice(context);
    }

}
