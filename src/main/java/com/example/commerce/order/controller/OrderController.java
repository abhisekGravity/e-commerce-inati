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
     * - Idempotency-Key
     */
    @PostMapping
    public Order placeOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return orderService.placeOrder(idempotencyKey);
    }
}