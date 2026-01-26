package com.example.ecommerce.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreateRequest {

    @NotBlank(message = "Tenant name cannot be empty")
    @Size(min = 3, max = 10, message = "Tenant name must be between 3 and 10 characters")
    private String name;
}