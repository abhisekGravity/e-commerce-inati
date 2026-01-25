package com.example.commerce.product;

import com.example.commerce.product.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String name,
            @RequestParam(required = false)
            @DecimalMin("0.0") BigDecimal minPrice,
            @RequestParam(required = false)
            @DecimalMin("0.01") BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "PRICE") @NotNull ProductSortField sortBy,
            @RequestParam(defaultValue = "ASC") @NotNull Sort.Direction direction,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset
    ) {
        ProductFilter filter = ProductFilter.builder()
                .sku(sku)
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .build();

        return service.list(filter, sortBy, direction, limit, offset);
    }
}