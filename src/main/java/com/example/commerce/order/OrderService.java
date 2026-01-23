package com.example.commerce.order;

import com.example.commerce.cart.Cart;
import com.example.commerce.cart.CartItem;
import com.example.commerce.cart.CartService;
import com.example.commerce.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;

    public Order placeOrder(String userId) {

        Cart cart = cartService.getActiveCart(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        try {
            for (CartItem item : cart.getItems()) {

                var product = inventoryService.decrementInventory(
                        item.getProductId(),
                        item.getQuantity()
                );

                if (product == null) {
                    throw new RuntimeException("Insufficient inventory");
                }

                OrderItem oi = new OrderItem();
                oi.setProductId(item.getProductId());
                oi.setQuantity(item.getQuantity());
                oi.setPrice(item.getPrice());

                orderItems.add(oi);
                total = total.add(
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setItems(orderItems);
            order.setTotal(total);
            order.setStatus("CREATED");
            order.setTenant();

            cartService.deactivateCart(cart);

            return orderRepository.save(order);

        } catch (Exception ex) {
            for (CartItem item : cart.getItems()) {
                inventoryService.incrementInventory(
                        item.getProductId(),
                        item.getQuantity()
                );
            }
            throw ex;
        }
    }
}
