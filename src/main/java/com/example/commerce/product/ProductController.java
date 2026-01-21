package com.example.commerce.product;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    @PostMapping
    public Product create(@RequestBody Product product) {
        return service.create(product);
    }

    @GetMapping
    public Page<ProductResponse> list(Pageable pageable) {
        return service.list(pageable);
    }
}
