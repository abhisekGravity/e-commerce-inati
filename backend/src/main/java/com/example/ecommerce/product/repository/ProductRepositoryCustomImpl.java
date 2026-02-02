package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.repository.query.ProductQueryBuilder;
import com.example.ecommerce.product.dto.ProductFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;


import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl
        implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> findByFilter(
            ProductFilter filter,
            int limit,
            int offset,
            Sort sort
    ) {

        Query dataQuery = ProductQueryBuilder.build(
                filter, sort, limit, offset);

        Query countQuery = ProductQueryBuilder.countQuery(filter);

        long total = mongoTemplate.count(countQuery, Product.class);
        List<Product> products =
                mongoTemplate.find(dataQuery, Product.class);

        Pageable pageable =
                PageRequest.of(offset / limit, limit, sort);

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public UpdateResult decrementInventory(String productId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(productId)
                .and("inventory").gte(quantity));

        Update update = new Update().inc("inventory", -quantity);

        return mongoTemplate.updateFirst(query, update, Product.class);
    }
}