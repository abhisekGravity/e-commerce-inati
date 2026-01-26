package com.example.ecommerce.tenant.controller;

import com.example.ecommerce.tenant.domain.Tenant;
import com.example.ecommerce.tenant.service.TenantService;
import com.example.ecommerce.tenant.dto.TenantCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantAdminController {

    private final TenantService tenantService;

    @PostMapping("/create")
    public Tenant create(@Valid @RequestBody TenantCreateRequest request) {
        return tenantService.createTenant(request.getName());
    }

    @GetMapping("/getAll")
    public List<Tenant> getAll() {
        return tenantService.getAllTenants();
    }
}
