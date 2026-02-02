package com.example.ecommerce.inventory.service;

import com.example.ecommerce.exception.order.InsufficientInventoryForOrderException;
import com.example.ecommerce.product.domain.Product;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final MongoTemplate mongoTemplate;

    public void reserveInventory(
            String tenantId,
            String sku,
            int quantity
    ) {
        Query query = new Query(
                Criteria.where("tenantId").is(tenantId)
                        .and("sku").is(sku)
                        .and("inventory").gte(quantity)
        );

        Update update = new Update()
                .inc("inventory", -quantity);

        UpdateResult result = mongoTemplate.updateFirst(
                query,
                update,
                Product.class
        );

        if (result.getModifiedCount() == 0) {
            throw new InsufficientInventoryForOrderException(sku);
        }
    }
}