package com.example.ecommerce.product.repo;

import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.dto.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface ProductRepositoryCustom {

    Page<Product> findByFilter(
            ProductFilter filter,
            int limit,
            int offset,
            Sort sort
    );
}
