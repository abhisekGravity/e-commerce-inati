package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.cart.domain.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.exception.cart.ActiveCartNotFoundException;
import com.example.ecommerce.exception.cart.ProductNotFoundForCartException;
import com.example.ecommerce.exception.cart.InsufficientInventoryException;
import com.example.ecommerce.pricing.dto.PricingContext;
import com.example.ecommerce.pricing.service.PricingService;
import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.security.util.SecurityUtil;
import com.example.ecommerce.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PricingService pricingService;

    public Cart addToCart( String sku, int quantity) {

        String userId = SecurityUtil.getCurrentUserId();
        String tenantId = TenantContext.getTenantId();

        Product product = productRepository
                .findByTenantIdAndSku(tenantId, sku)
                .orElseThrow(() -> new ProductNotFoundForCartException(sku));

        validateInventory(product, quantity);

        Cart cart = cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(tenantId, userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .active(true)
                        .build()
                );

        PricingContext pricingContext = PricingContext.builder()
                .tenantId(tenantId)
                .sku(sku)
                .basePrice(product.getBasePrice())
                .inventory(product.getInventory())
                .build();

        BigDecimal finalUnitPrice = pricingService.getFinalPrice(pricingContext);

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

    public Cart getActiveCart() {

        String userId = SecurityUtil.getCurrentUserId();

        return cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(
                        TenantContext.getTenantId(),
                        userId
                )
                .orElseThrow(ActiveCartNotFoundException::new);
    }

    public void clearCart(String userId) {
        Cart cart = cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(
                        TenantContext.getTenantId(),
                        userId
                )
                .orElseThrow(ActiveCartNotFoundException::new);

        cart.setActive(false);
        cartRepository.save(cart);
    }

    private void validateInventory(Product product, int quantity) {
        if (product.getInventory() < quantity) {
            throw new InsufficientInventoryException(
                    quantity,
                    product.getInventory()
            );
        }
    }
}
