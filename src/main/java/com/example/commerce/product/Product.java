package com.example.commerce.product;

import com.example.commerce.tenant.TenantContext;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@Document("products")
@CompoundIndex(def = "{'tenantId':1, 'sku':1}", unique = true)
public class Product {

    @Id
    private String id;

    private String tenantId;
    private String sku;
    private String name;
    private BigDecimal basePrice;
    private int inventory;

    public void setTenant() {
        if (tenantId == null) {
            tenantId = TenantContext.getTenantId();
        }
    }

    public void reduceInventory(int quantity) {
        if (inventory - quantity < 0) {
            throw new IllegalStateException("Insufficient inventory");
        }
        inventory -= quantity;
    }
}