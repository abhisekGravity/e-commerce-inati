package com.example.commerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository
        extends MongoRepository<Product, String> {

    Optional<Product> findByTenantIdAndSku(String tenantId, String sku);

    Page<Product> findByTenantId(String tenantId, Pageable pageable);
}
