package com.example.commerce.order;

import com.example.commerce.common.TenantAwareEntity;
import com.example.commerce.tenant.TenantContext;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document("orders")
@Data
public class Order extends TenantAwareEntity {

    @Id
    private String id;

    private String userId;

    private List<OrderItem> items;

    private BigDecimal total;
    private String status;

}
