package com.example.commerce.product;

import com.example.commerce.product.dto.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findByFilter(String tenantId, ProductFilter filter, Pageable pageable);
}
