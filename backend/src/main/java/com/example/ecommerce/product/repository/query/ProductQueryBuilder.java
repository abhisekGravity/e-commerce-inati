package com.example.ecommerce.product.repository.query;

import com.example.ecommerce.product.dto.ProductFilter;
import com.example.ecommerce.tenant.context.TenantContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductQueryBuilder {

    public static Query build(ProductFilter filter, Sort sort, int limit, int offset) {
        Query query = new Query();
        query.addCriteria(buildCriteria(filter));

        if (sort != null) {
            query.with(sort);
        }

        query.skip(offset);
        query.limit(limit);
        return query;
    }

    public static Query countQuery(ProductFilter filter) {
        Query query = new Query();
        query.addCriteria(buildCriteria(filter));
        return query;
    }

    private static Criteria buildCriteria(ProductFilter filter) {
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(Criteria.where("tenantId")
                .is(TenantContext.getTenantId()));

        if (filter.getSku() != null) {
            criteriaList.add(Criteria.where("sku")
                    .regex(filter.getSku(), "i"));
        }

        if (filter.getName() != null) {
            criteriaList.add(Criteria.where("name")
                    .regex(filter.getName(), "i"));
        }

        if (filter.getMinPrice() != null) {
            criteriaList.add(Criteria.where("basePrice")
                    .gte(filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("basePrice")
                    .lte(filter.getMaxPrice()));
        }

        if (filter.getInStock() != null) {
            if (filter.getInStock()) {
                criteriaList.add(Criteria.where("inventory").gt(0));
            } else {
                criteriaList.add(Criteria.where("inventory").is(0));
            }
        }

        return new Criteria().andOperator(
                criteriaList.toArray(new Criteria[0])
        );
    }
}