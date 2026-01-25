package com.example.commerce.product;

import com.example.commerce.product.dto.ProductFilter;
import com.example.commerce.tenant.TenantContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.regex.Pattern;

public final class ProductQueryBuilder {

    private ProductQueryBuilder() {}

    public static Query build(ProductFilter filter, Sort sort, int limit, int offset) {
        Query query = new Query();

        query.addCriteria(Criteria.where("tenantId").is(TenantContext.getTenantId()));

        if (hasText(filter.getSku())) {
            query.addCriteria(Criteria.where("sku").is(filter.getSku()));
        }

        if (hasText(filter.getName())) {
            query.addCriteria(Criteria.where("name")
                    .regex(".*" + Pattern.quote(filter.getName()) + ".*", "i"));
        }

        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            Criteria price = Criteria.where("basePrice");
            if (filter.getMinPrice() != null) price.gte(filter.getMinPrice());
            if (filter.getMaxPrice() != null) price.lte(filter.getMaxPrice());
            query.addCriteria(price);
        }

        if (Boolean.TRUE.equals(filter.getInStock())) {
            query.addCriteria(Criteria.where("inventory").exists(true).gt(0));
        }

        query.with(sort).skip(offset).limit(limit);

        return query;
    }

    public static Query countQuery(ProductFilter filter) {
        Query query = build(filter, Sort.unsorted(), 0, 0);
        query.skip(0).limit(0);
        return query;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}