package com.example.commerce.order.controller;

import com.example.commerce.order.domain.Order;
import com.example.commerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Place order (idempotent)
     *
     * Required headers:
     * - X-User-Id
     * - X-Tenant-Id (resolved by middleware)
     * - Idempotency-Key
     */
    @PostMapping
    public Order placeOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return orderService.placeOrder(userId, idempotencyKey);
    }
}