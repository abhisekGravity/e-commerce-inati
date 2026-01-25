package com.example.commerce.product;

import com.example.commerce.common.TenantAwareEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@Document("products")
@CompoundIndex(
        name = "tenant_sku_unique_idx",
        def = "{'tenantId': 1, 'sku': 1}",
        unique = true
)
@CompoundIndex(
        name = "tenant_price_idx",
        def = "{'tenantId': 1, 'basePrice': 1}"
)
@CompoundIndex(
        name = "tenant_inventory_idx",
        def = "{'tenantId': 1, 'inventory': 1}"
)
public class Product extends TenantAwareEntity {

    @Id
    private String id;

    private String sku;
    private String name;
    private BigDecimal basePrice;
    private int inventory;
}
