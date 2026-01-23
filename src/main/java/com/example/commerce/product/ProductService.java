package com.example.commerce.product;

import com.example.commerce.pricing.PricingEngine;
import com.example.commerce.product.dto.CreateProductRequest;
import com.example.commerce.product.dto.ProductFilter;
import com.example.commerce.product.dto.ProductResponse;
import com.example.commerce.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final PricingEngine pricingEngine;

    public ProductResponse create(CreateProductRequest request) {

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .basePrice(request.getBasePrice())
                .inventory(request.getInventory())
                .build();

        product.setTenant();

        Product saved = repository.save(product);
        return toResponse(saved);
    }

    public Page<ProductResponse> list(ProductFilter filter, Pageable pageable) {
        return repository
                .findByFilter(TenantContext.getTenantId(), filter, pageable)
                .map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .inventory(product.getInventory())
                .price(pricingEngine.calculatePrice(product))
                .build();
    }
}