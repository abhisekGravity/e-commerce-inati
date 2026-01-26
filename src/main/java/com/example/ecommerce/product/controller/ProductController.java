package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.domain.ProductSortField;
import com.example.ecommerce.product.dto.*;
import com.example.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
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
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "PRICE") ProductSortField sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {

        ProductFilter filter = ProductFilter.builder()
                .sku(sku)
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .build();

        return service.list(
                filter, sortBy, direction, limit, offset);
        }
}