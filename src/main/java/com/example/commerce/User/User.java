package com.example.commerce.User;

import com.example.commerce.tenant.TenantContext;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@CompoundIndex(def = "{'tenantId':1, 'email':1}", unique = true)
@Data
public class User {

    @Id
    private String id;

    private String tenantId;

    private String email;
    private String passwordHash;

    public void setTenant() {
        if (tenantId == null) {
            tenantId = TenantContext.getTenantId();
        }
    }
}
