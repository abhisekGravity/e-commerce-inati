package com.example.ecommerce;

import com.example.ecommerce.exception.product.InvalidProductRequestException;
import com.example.ecommerce.exception.product.ProductAlreadyExistsException;
import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.domain.ProductSortField;
import com.example.ecommerce.product.dto.*;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.product.service.ProductService;
import com.example.ecommerce.tenant.context.TenantContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @BeforeEach
    void setup() {
        TenantContext.setTenantId("tenant-1");
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void create_successfullyCreatesProduct() {
        CreateProductRequest request = CreateProductRequest.builder()
                .sku("SKU-1")
                .name("iPhone")
                .basePrice(BigDecimal.valueOf(1000))
                .inventory(10)
                .build();

        when(repository.existsByTenantIdAndSku("tenant-1", "SKU-1")).thenReturn(false);
        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId("product-1");
            return p;
        });

        ProductResponse response = service.create(request);

        assertThat(response.getSku()).isEqualTo("SKU-1");
        assertThat(response.getName()).isEqualTo("iPhone");
        assertThat(response.getPrice()).isEqualByComparingTo("1000");
        assertThat(response.getInventory()).isEqualTo(10);

        verify(repository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertThat(saved.getSku()).isEqualTo("SKU-1");
    }

    @Test
    void create_throwsExceptionWhenProductAlreadyExists() {
        CreateProductRequest request = CreateProductRequest.builder()
                .sku("SKU-1")
                .build();

        when(repository.existsByTenantIdAndSku("tenant-1", "SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ProductAlreadyExistsException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void list_returnsPagedProducts() {
        ProductFilter filter = ProductFilter.builder().build();

        Product product = Product.builder()
                .id("p1")
                .sku("SKU-1")
                .name("Phone")
                .basePrice(BigDecimal.valueOf(500))
                .inventory(5)
                .build();

        Page<Product> page = new PageImpl<>(List.of(product));

        when(repository.findByFilter(any(), eq(20), eq(0), any(Sort.class))).thenReturn(page);

        Page<ProductResponse> result = service.list(
                filter,
                ProductSortField.PRICE,
                Sort.Direction.ASC,
                20,
                0
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSku()).isEqualTo("SKU-1");
    }

    @Test
    void list_throwsExceptionWhenMinPriceNegative() {
        ProductFilter filter = ProductFilter.builder()
                .minPrice(BigDecimal.valueOf(-1))
                .build();

        assertThatThrownBy(() ->
                service.list(filter, ProductSortField.PRICE, Sort.Direction.ASC, 10, 0))
                .isInstanceOf(InvalidProductRequestException.class)
                .hasMessageContaining("minPrice cannot be negative");
    }

    @Test
    void list_throwsExceptionWhenMaxPriceZeroOrNegative() {
        ProductFilter filter = ProductFilter.builder()
                .maxPrice(BigDecimal.ZERO)
                .build();

        assertThatThrownBy(() ->
                service.list(filter, ProductSortField.PRICE, Sort.Direction.ASC, 10, 0))
                .isInstanceOf(InvalidProductRequestException.class)
                .hasMessageContaining("maxPrice must be greater than zero");
    }

    @Test
    void list_throwsExceptionWhenMinPriceGreaterThanMaxPrice() {
        ProductFilter filter = ProductFilter.builder()
                .minPrice(BigDecimal.valueOf(200))
                .maxPrice(BigDecimal.valueOf(100))
                .build();

        assertThatThrownBy(() ->
                service.list(filter, ProductSortField.PRICE, Sort.Direction.ASC, 10, 0))
                .isInstanceOf(InvalidProductRequestException.class)
                .hasMessageContaining("minPrice cannot be greater than maxPrice");
    }

    @Test
    void list_throwsExceptionWhenOffsetNegative() {
        ProductFilter filter = ProductFilter.builder().build();

        assertThatThrownBy(() ->
                service.list(filter, ProductSortField.PRICE, Sort.Direction.ASC, 10, -1))
                .isInstanceOf(InvalidProductRequestException.class)
                .hasMessageContaining("offset cannot be negative");
    }

    @Test
    void list_throwsExceptionWhenLimitZeroOrNegative() {
        ProductFilter filter = ProductFilter.builder().build();

        assertThatThrownBy(() ->
                service.list(filter, ProductSortField.PRICE, Sort.Direction.ASC, 0, 0))
                .isInstanceOf(InvalidProductRequestException.class)
                .hasMessageContaining("limit must be greater than zero");
    }
}
