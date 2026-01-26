package com.example.ecommerce.product.repo;

import com.example.ecommerce.product.domain.Product;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ProductRepository
        extends MongoRepository<Product, String>, ProductRepositoryCustom {

    boolean existsByTenantIdAndSku(String tenantId, String sku);

    Optional<Product> findByTenantIdAndSku(String tenantId, String sku);

}
