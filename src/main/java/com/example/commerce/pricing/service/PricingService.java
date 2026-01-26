package com.example.commerce.pricing.service;

import com.example.commerce.pricing.dto.PricingContext;
import com.example.commerce.pricing.engine.PricingEngine;
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
}
