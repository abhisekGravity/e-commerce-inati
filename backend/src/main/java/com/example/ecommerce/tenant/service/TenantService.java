package com.example.ecommerce.tenant.service;

import com.example.ecommerce.exception.tenant.TenantAlreadyExistsException;
import com.example.ecommerce.tenant.domain.Tenant;
import com.example.ecommerce.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public Tenant createTenant(String name) {
        String trimmedName = name.trim();

        if (tenantRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new TenantAlreadyExistsException(trimmedName);
        }

        String slug = trimmedName.toLowerCase().replaceAll("\\s+", "_");

        Tenant tenant = Tenant.builder()
                .name(trimmedName)
                .tenantSlug(slug)
                .active(true)
                .build();

        return tenantRepository.save(tenant);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
}