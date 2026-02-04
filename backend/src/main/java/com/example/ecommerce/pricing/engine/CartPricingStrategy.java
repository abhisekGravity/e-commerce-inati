package com.example.ecommerce.pricing.engine;

import com.example.ecommerce.cart.domain.Cart;

public interface CartPricingStrategy {
    boolean isApplicable(Cart cart);
    void apply(Cart cart);
}
