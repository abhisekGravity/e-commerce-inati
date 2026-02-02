package com.example.ecommerce.pricing.config;

import com.example.ecommerce.pricing.engine.PricingStrategy;
import com.example.ecommerce.pricing.strategies.FlatDiscountStrategy;
import com.example.ecommerce.pricing.strategies.InventoryBasedPricingStrategy;
import com.example.ecommerce.pricing.strategies.PercentageDiscountStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class PricingStrategyConfig {

    @Bean
    public PercentageDiscountStrategy percentageDiscountStrategy() {
        return new PercentageDiscountStrategy(BigDecimal.valueOf(0.10));
    }

    @Bean
    public FlatDiscountStrategy flatDiscountStrategy() {
        return new FlatDiscountStrategy(BigDecimal.valueOf(5));
    }

    @Bean
    public InventoryBasedPricingStrategy inventoryBasedPricingStrategy() {
        return new InventoryBasedPricingStrategy(10, BigDecimal.valueOf(2));
    }

    @Bean
    public List<PricingStrategy> pricingStrategies(
            PercentageDiscountStrategy percentageDiscountStrategy,
            FlatDiscountStrategy flatDiscountStrategy,
            InventoryBasedPricingStrategy inventoryBasedPricingStrategy
    ) {
        return List.of(percentageDiscountStrategy, flatDiscountStrategy, inventoryBasedPricingStrategy);
    }
}
