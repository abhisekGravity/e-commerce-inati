package com.example.commerce.order.repository;

import com.example.commerce.order.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByTenantIdAndIdempotencyKey(
            String tenantId,
            String idempotencyKey
    );
}
