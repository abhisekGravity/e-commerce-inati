package com.example.commerce.cart.service;

import com.example.commerce.cart.domain.Cart;
import com.example.commerce.cart.domain.CartItem;
import com.example.commerce.cart.repository.CartRepository;
import com.example.commerce.pricing.dto.PricingContext;
import com.example.commerce.pricing.engine.PricingEngine;
import com.example.commerce.product.Product;
import com.example.commerce.product.repo.ProductRepository;
import com.example.commerce.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PricingEngine pricingEngine;

    public Cart addToCart(String userId, String sku, int quantity) {

        String tenantId = TenantContext.getTenantId();

        Product product = productRepository
                .findByTenantIdAndSku(tenantId, sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        validateInventory(product, quantity);

        Cart cart = cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(tenantId, userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .active(true)
                        .build()
                );

        PricingContext pricingContext = new PricingContext(
                tenantId,
                sku,
                product.getBasePrice(),
                product.getInventory()
        );

        BigDecimal finalUnitPrice = pricingEngine.calculatePrice(pricingContext);

        CartItem item = CartItem.builder()
                .productId(product.getId())
                .sku(sku)
                .name(product.getName())
                .quantity(quantity)
                .unitPrice(finalUnitPrice)
                .build();

        item.recalculate();

        cart.addOrUpdateItem(item);

        return cartRepository.save(cart);
    }

    public Cart getActiveCart(String userId) {
        return cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(
                        TenantContext.getTenantId(),
                        userId
                )
                .orElseThrow(() -> new IllegalStateException("No active cart"));
    }

    private void validateInventory(Product product, int quantity) {
        if (product.getInventory() < quantity) {
            throw new IllegalStateException("Insufficient inventory");
        }
    }
}
