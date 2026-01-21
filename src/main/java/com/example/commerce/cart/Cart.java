package com.example.commerce.cart;

import com.example.commerce.tenant.TenantContext;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("carts")
@Data
public class Cart {

    @Id
    private String id;

    private String tenantId;
    private String userId;

    private boolean active = true;

    private List<CartItem> items = new ArrayList<>();

    public void setTenant() {
        if (tenantId == null) {
            tenantId = TenantContext.getTenantId();
        }
    }
}
