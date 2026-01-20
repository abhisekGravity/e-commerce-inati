package com.example.commerce.product;

import com.example.commerce.pricing.PricingEngine;
import com.example.commerce.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository repository;
    private final PricingEngine pricingEngine;



    public Product create(Product product) {
        product.setTenant();
        return repository.save(product);
    }

    public Page<ProductResponse> list(Pageable pageable) {
        return repository
                .findByTenantId(TenantContext.getTenantId(), pageable)
                .map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        BigDecimal finalPrice = pricingEngine.calculatePrice(product);
        return new ProductResponse(product, finalPrice);
    }
}
