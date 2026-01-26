package com.example.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateProductRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Base price must be greater than zero")
    private BigDecimal basePrice;

    @Min(value = 0, message = "Inventory cannot be negative")
    private int inventory;
}
