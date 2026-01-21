package com.example.commerce.tenant;

import com.example.commerce.tenant.dto.TenantCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/create")
    public Tenant create(@RequestBody TenantCreateRequest request) {
        return tenantService.createTenant(request.getName());
    }

    @GetMapping("/getAll")
    public List<Tenant> getAll() {
        return tenantService.getAllTenants();
    }
}
