package com.example.ecommerce.order.service;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.cart.domain.CartItem;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.domain.OrderItem;
import com.example.ecommerce.order.domain.OrderStatus;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.security.util.SecurityUtil;
import com.example.ecommerce.tenant.context.TenantContext;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Order placeOrder(String idempotencyKey) {

        String tenantId = TenantContext.getTenantId();
        String userId = SecurityUtil.getCurrentUserId();

        Optional<Order> existing = orderRepository
                .findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey);

        if (existing.isPresent()) {
            return existing.get();
        }

        Cart cart = cartService.getCart();
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {

            UpdateResult result = productRepository
                    .decrementInventory(item.getProductId(), item.getQuantity());

            if (result.getModifiedCount() == 0) {
                throw new IllegalStateException(
                        "Insufficient stock for SKU: " + item.getSku());
            }

            OrderItem orderItem = OrderItem.builder()
                    .productId(item.getProductId())
                    .sku(item.getSku())
                    .name(item.getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .baseUnitPrice(item.getBaseUnitPrice())
                    .totalPrice(
                            item.getUnitPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();

            orderItems.add(orderItem);
            total = total.add(orderItem.getTotalPrice());
        }

        Order order = Order.builder()
                .tenantId(tenantId)
                .userId(userId)
                .idempotencyKey(idempotencyKey)
                .status(OrderStatus.CREATED)
                .items(orderItems)
                .subtotal(cart.getSubtotal())
                .discountAmount(cart.getDiscountAmount())
                .discountName(cart.getDiscountName())
                .totalAmount(cart.getTotalPrice())
                .build();

        orderRepository.save(order);

        cartService.clearCart(userId);

        return order;
    }
}
