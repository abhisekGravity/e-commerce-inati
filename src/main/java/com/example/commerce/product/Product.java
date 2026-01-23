package com.example.commerce.product;

import com.example.commerce.common.TenantAwareEntity;
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
@CompoundIndex(
        name = "tenant_sku_unique_idx",
        def = "{'tenantId': 1, 'sku': 1}",
        unique = true
)
public class Product extends TenantAwareEntity {

    @Id
    private String id;

    private String sku;
    private String name;
    private BigDecimal basePrice;
    private int inventory;

}