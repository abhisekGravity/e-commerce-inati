package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.dto.ProductFilter;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface ProductRepositoryCustom {

    Page<Product> findByFilter(
            ProductFilter filter,
            int limit,
            int offset,
            Sort sort
    );

    UpdateResult decrementInventory(String productId, int quantity);
}
