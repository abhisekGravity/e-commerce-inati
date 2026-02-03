package com.example.ecommerce.order.domain;

import com.example.ecommerce.common.tenant.TenantAwareEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order extends TenantAwareEntity {

    @Id
    private String id;

    private String userId;

    private String idempotencyKey;

    private OrderStatus status;

    private List<OrderItem> items;

    private BigDecimal subtotal;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;
}
