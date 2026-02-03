package com.example.ecommerce.User.domain;

import com.example.ecommerce.common.tenant.TenantAwareEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@CompoundIndex(name = "tenant_email_unique_idx", def = "{'tenantId': 1, 'email': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends TenantAwareEntity {

    @Id
    private String id;

    private String email;
    private String passwordHash;

}
