package com.example.commerce.cart.controller;

import com.example.commerce.cart.domain.Cart;
import com.example.commerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Cart addToCart(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestHeader("X-User-Id") String userId
    ) {
        return cartService.addToCart(userId, sku, quantity);
    }

    @GetMapping
    public Cart getCart(
            @RequestHeader("X-User-Id") String userId
    ) {
        return cartService.getActiveCart(userId);
    }
}
