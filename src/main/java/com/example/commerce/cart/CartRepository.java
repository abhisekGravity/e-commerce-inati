package com.example.commerce.cart;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByTenantIdAndUserIdAndActiveTrue(String tenantId, String userId);
}
