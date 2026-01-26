package com.example.ecommerce.product.domain;

import com.example.ecommerce.common.tenant.TenantAwareEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@Builder
@Document(collection = "products")
public class Product extends TenantAwareEntity {

    @Id
    private String id;

    private String sku;

    private String name;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal basePrice;

    private int inventory;

    @Version
    private Long version;
}
