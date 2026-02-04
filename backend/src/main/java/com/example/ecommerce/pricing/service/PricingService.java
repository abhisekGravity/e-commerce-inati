package com.example.ecommerce.pricing.service;

import com.example.ecommerce.pricing.dto.PricingContext;
import com.example.ecommerce.pricing.engine.PricingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingEngine pricingEngine;

    public BigDecimal getFinalPrice(PricingContext context) {
        return pricingEngine.calculatePrice(context);
    }

    public void applyCartDiscounts(com.example.ecommerce.cart.domain.Cart cart) {
        pricingEngine.applyCartDiscounts(cart);
    }
}
