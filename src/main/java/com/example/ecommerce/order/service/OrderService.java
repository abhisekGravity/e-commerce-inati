package com.example.ecommerce.order.service;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.cart.domain.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.exception.order.*;
import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.domain.OrderItem;
import com.example.ecommerce.order.domain.OrderStatus;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.repo.ProductRepository;
import com.example.ecommerce.security.util.SecurityUtil;
import com.example.ecommerce.tenant.context.TenantContext;
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
                .orElseThrow(ActiveCartNotFoundForOrderException::new);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        // 3️⃣ Atomic inventory deduction (optimistic locking)
        for (CartItem item : cart.getItems()) {

            Product product = productRepository
                    .findByTenantIdAndSku(tenantId, item.getSku())
                    .orElseThrow(() -> new ProductNotFoundForOrderException(item.getSku()));

            if (product.getInventory() < item.getQuantity()) {
                throw new InsufficientInventoryForOrderException(item.getSku());
            }

            product.setInventory(
                    product.getInventory() - item.getQuantity()
            );

            try {
                productRepository.save(product);
            } catch (OptimisticLockingFailureException ex) {
                throw new InventoryConcurrencyException(item.getSku());
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
