package com.example.commerce.product.repo;

import com.example.commerce.product.domain.Product;
import com.example.commerce.product.dto.ProductFilter;
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
