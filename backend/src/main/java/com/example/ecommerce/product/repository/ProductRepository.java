package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository
        extends MongoRepository<Product, String>, ProductRepositoryCustom {

    boolean existsByTenantIdAndSku(String tenantId, String sku);

    Optional<Product> findByTenantIdAndSku(String tenantId, String sku);

}
