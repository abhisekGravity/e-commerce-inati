package com.example.ecommerce.order.controller;

import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order placeOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return orderService.placeOrder(idempotencyKey);
    }
}