package com.example.commerce.order;

import com.example.commerce.tenant.TenantContext;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document("orders")
@Data
public class Order {

    @Id
    private String id;

    private String tenantId;
    private String userId;

    private List<OrderItem> items;

    private BigDecimal total;
    private String status;

    private Instant createdAt = Instant.now();

    public void setTenant() {
        if (tenantId == null) {
            tenantId = TenantContext.getTenantId();
        }
    }
}
