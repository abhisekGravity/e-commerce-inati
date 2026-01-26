package com.example.commerce.product;

import com.example.commerce.product.dto.*;
import com.example.commerce.product.repo.ProductRepository;
import com.example.commerce.tenant.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponse create(@Valid CreateProductRequest request) {
        String tenantId = TenantContext.getTenantId();

        if (repository.existsByTenantIdAndSku(tenantId, request.getSku())) {
            throw new IllegalArgumentException("SKU already exists for tenant");
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .basePrice(request.getBasePrice())
                .inventory(request.getInventory())
                .build();

        return toResponse(repository.save(product));
    }

    public Page<ProductResponse> list(
            ProductFilter filter,
            ProductSortField sortBy,
            Sort.Direction direction,
            int limit,
            int offset
    ) {

        if (filter.getMinPrice() != null &&
                filter.getMaxPrice() != null &&
                filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {
            throw new IllegalArgumentException(
                    "minPrice cannot be greater than maxPrice"
            );
        }

        Sort sort = Sort.by(direction, sortBy.getField());

        return repository.findByFilter(filter, limit, offset, sort)
                .map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getBasePrice())
                .inventory(product.getInventory())
                .build();
    }
}