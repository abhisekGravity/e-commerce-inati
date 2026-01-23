package com.example.commerce.cart;

import com.example.commerce.pricing.PricingEngine;
import com.example.commerce.product.Product;
import com.example.commerce.product.repo.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class CartMapper {

    private final ProductRepository productRepository;
    private final PricingEngine pricingEngine;

    public CartResponse toResponse(Cart cart) {
        CartResponse response = new CartResponse();

        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            BigDecimal price = pricingEngine.calculatePrice(product);

            CartItemResponse dto = new CartItemResponse();
            dto.setProductId(product.getId());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(price);
            dto.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            return dto;
        }).collect(Collectors.toList());

        response.setItems(items);

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotal(total);

        return response;
    }
}
