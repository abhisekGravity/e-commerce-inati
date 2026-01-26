package com.example.commerce.order.service;

import com.example.commerce.cart.domain.Cart;
import com.example.commerce.cart.domain.CartItem;
import com.example.commerce.cart.repository.CartRepository;
import com.example.commerce.order.domain.Order;
import com.example.commerce.order.domain.OrderItem;
import com.example.commerce.order.domain.OrderStatus;
import com.example.commerce.order.repository.OrderRepository;
import com.example.commerce.product.Product;
import com.example.commerce.product.repo.ProductRepository;
import com.example.commerce.security.SecurityUtil;
import com.example.commerce.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Idempotent + transactional order placement
     */
    @Transactional
    public Order placeOrder( String idempotencyKey) {

        String userId = SecurityUtil.getCurrentUserId();
        String tenantId = TenantContext.getTenantId();

        // 1️⃣ Idempotency check (VERY IMPORTANT)
        return orderRepository
                .findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey)
                .orElseGet(() -> createOrderInternal(userId, idempotencyKey));
    }

    /**
     * Internal order creation logic
     * Executed only once per idempotency key
     */
    private Order createOrderInternal(String userId, String idempotencyKey) {

        String tenantId = TenantContext.getTenantId();

        // 2️⃣ Fetch active cart
        Cart cart = cartRepository
                .findByTenantIdAndUserIdAndActiveTrue(tenantId, userId)
                .orElseThrow(() -> new IllegalStateException("No active cart found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // 3️⃣ Atomic inventory deduction (optimistic locking)
        for (CartItem item : cart.getItems()) {

            Product product = productRepository
                    .findByTenantIdAndSku(tenantId, item.getSku())
                    .orElseThrow(() ->
                            new IllegalStateException("Product not found: " + item.getSku())
                    );

            if (product.getInventory() < item.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient inventory for SKU: " + item.getSku()
                );
            }

            product.setInventory(
                    product.getInventory() - item.getQuantity()
            );

            try {
                productRepository.save(product);
            } catch (OptimisticLockingFailureException ex) {
                throw new IllegalStateException(
                        "Concurrent inventory update detected for SKU: " + item.getSku()
                );
            }
        }

        // 4️⃣ Convert Cart → Order snapshot
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(this::toOrderItem)
                .toList();

        Order order = Order.builder()
                .userId(userId)
                .idempotencyKey(idempotencyKey)
                .status(OrderStatus.CREATED)
                .items(orderItems)
                .totalAmount(cart.getTotalPrice())
                .build();

        // 5️⃣ Persist order
        Order savedOrder = orderRepository.save(order);

        // 6️⃣ Deactivate cart
        cart.setActive(false);
        cartRepository.save(cart);

        return savedOrder;
    }

    private OrderItem toOrderItem(CartItem item) {
        return OrderItem.builder()
                .productId(item.getProductId())
                .sku(item.getSku())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
