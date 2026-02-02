package com.example.ecommerce.common.tenant;

import com.example.ecommerce.tenant.context.TenantContext;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TenantAwareCallback
        implements BeforeConvertCallback<TenantAwareEntity> {

    @Override
    public TenantAwareEntity onBeforeConvert(
            TenantAwareEntity entity,
            String collection
    ) {
        if (entity.getTenantId() == null) {
            entity.setTenantId(TenantContext.getTenantId());
        }

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }

        return entity;
    }
}
