package com.example.ecommerce.cart.domain;

import com.example.ecommerce.common.tenant.TenantAwareEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
@CompoundIndex(name = "unique_active_cart_per_user_tenant", def = "{'tenantId': 1, 'userId': 1, 'active': 1}", unique = true)
public class Cart extends TenantAwareEntity {

    @Id
    private String id;

    private String userId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String discountName;

    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public void addOrUpdateItem(CartItem newItem) {
        items.removeIf(i -> i.getSku().equals(newItem.getSku()));
        items.add(newItem);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.subtotal = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.discountAmount = BigDecimal.ZERO;
        this.totalPrice = this.subtotal;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
