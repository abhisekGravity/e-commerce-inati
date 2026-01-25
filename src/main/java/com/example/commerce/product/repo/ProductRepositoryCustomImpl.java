package com.example.commerce.product.repo;

import com.example.commerce.product.Product;
import com.example.commerce.product.ProductQueryBuilder;
import com.example.commerce.product.dto.ProductFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> findByFilter(ProductFilter filter, int limit, int offset, Sort sort) {

        Query dataQuery = ProductQueryBuilder.build(filter, sort, limit, offset);
        Query countQuery = ProductQueryBuilder.countQuery(filter);

        long total = mongoTemplate.count(countQuery, Product.class);
        List<Product> products = mongoTemplate.find(dataQuery, Product.class);

        Pageable pageable = PageRequest.of(offset / limit, limit, sort);
        return new PageImpl<>(products, pageable, total);
    }
}