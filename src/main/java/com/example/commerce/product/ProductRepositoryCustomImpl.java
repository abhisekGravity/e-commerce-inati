package com.example.commerce.product;

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
    public Page<Product> findByFilter(
            String tenantId,
            ProductFilter filter,
            Pageable pageable
    ) {

        Query query = new Query(
                ProductCriteriaBuilder.build(tenantId, filter)
        );

        long total = mongoTemplate.count(query, Product.class);

        query.with(pageable);
        List<Product> products = mongoTemplate.find(query, Product.class);

        return new PageImpl<>(products, pageable, total);
    }
}
