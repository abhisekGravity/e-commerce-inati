package com.example.commerce.cart;

import com.example.commerce.pricing.PricingEngine;
import com.example.commerce.product.Product;
import com.example.commerce.product.ProductRepository;
import com.example.commerce.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PricingEngine pricingEngine;

    public Cart getOrCreateCart() {
        String userId = currentUser();

        return cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(TenantContext.getTenantId(), userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setTenant();
                    return cartRepository.save(cart);
                });
    }

    public Cart addItem(String productId, int quantity) {
        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity > product.getInventory()) {
            throw new RuntimeException("Insufficient inventory");
        }

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProductId(productId);
                    newItem.setQuantity(0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        if (item.getQuantity() + quantity > product.getInventory()) {
            throw new RuntimeException("Insufficient inventory");
        }

        item.setQuantity(item.getQuantity() + quantity);

        return cartRepository.save(cart);
    }

    public Cart viewCart() {
        return getOrCreateCart();
    }

    private String currentUser() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
