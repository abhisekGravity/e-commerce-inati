package com.example.commerce.product.repo;

import com.example.commerce.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;

public interface ProductRepository
        extends MongoRepository<Product, String>, ProductRepositoryCustom {

    boolean existsByTenantIdAndSku(String tenantId, String sku);

    Optional<Product> findByTenantIdAndSku(String tenantId, String sku);
}
