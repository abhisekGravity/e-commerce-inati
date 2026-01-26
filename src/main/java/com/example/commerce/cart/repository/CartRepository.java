package com.example.commerce.cart.repository;

import com.example.commerce.cart.domain.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByTenantIdAndUserIdAndActiveTrue(
            String tenantId,
            String userId
    );
}
