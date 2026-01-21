package com.example.commerce.inventory;

import com.example.commerce.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final MongoTemplate mongoTemplate;

    public Product decrementInventory(String productId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(productId)
                .and("inventory").gte(quantity));

        Update update = new Update().inc("inventory", -quantity);

        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Product.class
        );
    }

    public void incrementInventory(String productId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(productId));
        Update update = new Update().inc("inventory", quantity);
        mongoTemplate.updateFirst(query, update, Product.class);
    }
}
