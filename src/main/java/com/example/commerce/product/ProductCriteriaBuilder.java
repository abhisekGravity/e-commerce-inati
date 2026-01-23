package com.example.commerce.product;

import com.example.commerce.product.dto.ProductFilter;
import org.springframework.data.mongodb.core.query.Criteria;

public final class ProductCriteriaBuilder {

    private ProductCriteriaBuilder() {}

    public static Criteria build(String tenantId, ProductFilter filter) {

        Criteria criteria = Criteria.where("tenantId").is(tenantId);

        if (filter.getSku() != null) {
            criteria.and("sku").is(filter.getSku());
        }

        if (filter.getName() != null) {
            criteria.and("name").regex(filter.getName(), "i");
        }

        if (filter.getMinPrice() != null) {
            criteria.and("basePrice").gte(filter.getMinPrice());
        }

        if (filter.getMaxPrice() != null) {
            criteria.and("basePrice").lte(filter.getMaxPrice());
        }

        if (Boolean.TRUE.equals(filter.getInStock())) {
            criteria.and("inventory").gt(0);
        }

        return criteria;
    }
}
