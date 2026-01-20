package com.example.commerce.product;

import com.example.commerce.tenant.TenantContext;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document("products")
@CompoundIndex(def = "{'tenantId':1, 'sku':1}", unique = true)
@Data
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
}
