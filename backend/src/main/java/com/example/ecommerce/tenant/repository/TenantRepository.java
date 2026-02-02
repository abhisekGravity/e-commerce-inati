package com.example.ecommerce.tenant.repository;

import com.example.ecommerce.tenant.domain.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TenantRepository extends MongoRepository<Tenant, String> {

    Optional<Tenant> findByTenantSlug(String tenantSlug);

    boolean existsByNameIgnoreCase(String name);
}