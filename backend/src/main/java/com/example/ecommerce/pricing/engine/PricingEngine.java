package com.example.ecommerce.pricing.engine;

import com.example.ecommerce.pricing.dto.PricingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PricingEngine {

    private final List<PricingStrategy> strategies;
    private final List<CartPricingStrategy> cartStrategies;

    public BigDecimal calculatePrice(PricingContext context) {
        BigDecimal price = context.basePrice();

        for (PricingStrategy strategy : strategies) {
            if (strategy.isApplicable(context)) {
                price = strategy.apply(new PricingContext(
                        context.tenantId(),
                        context.sku(),
                        price,
                        context.inventory()
                ));
            }
        }

        return price;
    }

    public void applyCartDiscounts(com.example.ecommerce.cart.domain.Cart cart) {
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setTotalPrice(cart.getSubtotal());

        for (CartPricingStrategy strategy : cartStrategies) {
            if (strategy.isApplicable(cart)) {
                strategy.apply(cart);
            }
        }
    }
}