package com.example.commerce.product;

import com.example.commerce.product.dto.CreateProductRequest;
import com.example.commerce.product.dto.ProductFilter;
import com.example.commerce.product.dto.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ProductResponse create(
            @Valid @RequestBody CreateProductRequest request
    ) {
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

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        ProductFilter filter = ProductFilter.builder()
                .sku(sku)
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .build();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy.getField())
        );

        return service.list(filter, pageable);
    }
}
