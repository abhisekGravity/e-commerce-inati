package com.example.ecommerce.common.tenant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class TenantAwareEntity {

    protected String tenantId;

    private Instant createdAt;
}
