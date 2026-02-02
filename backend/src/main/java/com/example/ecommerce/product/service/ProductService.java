package com.example.ecommerce.product.service;

import com.example.ecommerce.exception.product.InvalidProductRequestException;
import com.example.ecommerce.exception.product.ProductAlreadyExistsException;
import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.domain.ProductSortField;
import com.example.ecommerce.product.dto.*;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.tenant.context.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponse create(@Valid CreateProductRequest request) {
        String tenantId = TenantContext.getTenantId();

        if (repository.existsByTenantIdAndSku(tenantId, request.getSku())) {
            throw new ProductAlreadyExistsException(request.getSku(), tenantId);
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

        validateProductRequest(filter, sortBy, limit, offset);

        Sort sort = Sort.by(direction, sortBy.getField());

        return repository.findByFilter(filter, limit, offset, sort)
                .map(this::toResponse);
    }

    private void validateProductRequest(
            ProductFilter filter,
            ProductSortField sortBy,
            int limit,
            int offset
    ) {
        if (filter.getMinPrice() != null && filter.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductRequestException("minPrice cannot be negative");
        }

        if (filter.getMaxPrice() != null && filter.getMaxPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductRequestException("maxPrice must be greater than zero");
        }

        if (filter.getMinPrice() != null && filter.getMaxPrice() != null &&
                filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {
            throw new InvalidProductRequestException("minPrice cannot be greater than maxPrice");
        }

        boolean validSort = Arrays.stream(ProductSortField.values())
                .anyMatch(f -> f.name().equals(sortBy.name()));
        if (!validSort) {
            throw new InvalidProductRequestException("Invalid sort field. Allowed fields: " +
                    Arrays.toString(ProductSortField.values()));
        }

        if (offset < 0) {
            throw new InvalidProductRequestException("offset cannot be negative");
        }
        if (limit <= 0) {
            throw new InvalidProductRequestException("limit must be greater than zero");
        }
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