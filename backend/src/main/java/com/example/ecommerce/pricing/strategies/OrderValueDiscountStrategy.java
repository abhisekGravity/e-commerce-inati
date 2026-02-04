package com.example.ecommerce.pricing.strategies;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.pricing.engine.CartPricingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderValueDiscountStrategy implements CartPricingStrategy {

    private static final BigDecimal THRESHOLD = BigDecimal.valueOf(100);
    private static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.10);

    @Override
    public boolean isApplicable(Cart cart) {
        return cart.getSubtotal().compareTo(THRESHOLD) > 0;
    }

    @Override
    public void apply(Cart cart) {
        BigDecimal discountAmount = cart.getSubtotal().multiply(DISCOUNT_RATE);
        cart.setDiscountAmount(discountAmount);
        cart.setDiscountName("10% Off Order > $100");
        cart.setTotalPrice(cart.getSubtotal().subtract(discountAmount));
    }
}
