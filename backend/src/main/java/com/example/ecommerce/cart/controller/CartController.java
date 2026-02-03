package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.cart.service.CartService;
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
            @RequestParam int quantity
    ) {
        return cartService.addToCart( sku, quantity);
    }

    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }
}
