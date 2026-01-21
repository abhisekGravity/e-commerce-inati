package com.example.commerce.cart;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @PostMapping("/items")
    public CartResponse addItem(
            @RequestParam String productId,
            @RequestParam int quantity) {

        Cart cart = cartService.addItem(productId, quantity);
        return cartMapper.toResponse(cart);
    }

    @GetMapping
    public CartResponse view() {
        return cartMapper.toResponse(cartService.viewCart());
    }
}
