package com.example.ecommerce;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.cart.domain.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.exception.cart.*;
import com.example.ecommerce.pricing.dto.PricingContext;
import com.example.ecommerce.pricing.service.PricingService;
import com.example.ecommerce.product.domain.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.security.util.SecurityUtil;
import com.example.ecommerce.tenant.context.TenantContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private CartService cartService;

    private MockedStatic<SecurityUtil> securityUtilMock;

    @BeforeEach
    void setup() {
        TenantContext.setTenantId("tenant-1");
        securityUtilMock = mockStatic(SecurityUtil.class);
        securityUtilMock.when(SecurityUtil::getCurrentUserId)
                .thenReturn("user-1");
    }

    @AfterEach
    void cleanup() {
        securityUtilMock.close();
        TenantContext.clear();
    }

    @Test
    void addToCart_createsNewCartWhenNoneExists() {
        Product product = Product.builder()
                .id("p1")
                .sku("SKU-1")
                .name("iPhone")
                .basePrice(BigDecimal.valueOf(1000))
                .inventory(10)
                .build();

        when(productRepository.findByTenantIdAndSku("tenant-1", "SKU-1"))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByTenantIdAndUserId("tenant-1", "user-1"))
                .thenReturn(Optional.empty());

        when(pricingService.getFinalPrice(any(PricingContext.class)))
                .thenReturn(BigDecimal.valueOf(900));

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = cartService.addToCart("SKU-1", 2);

        assertThat(cart.getItems()).hasSize(1);
        CartItem item = cart.getItems().get(0);
        assertThat(item.getSku()).isEqualTo("SKU-1");
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void addToCart_updatesExistingCart() {
        Product product = Product.builder()
                .id("p1")
                .sku("SKU-1")
                .name("iPhone")
                .basePrice(BigDecimal.valueOf(1000))
                .inventory(10)
                .build();

        Cart existingCart = Cart.builder()
                .userId("user-1")
                .build();

        when(productRepository.findByTenantIdAndSku(any(), any()))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByTenantIdAndUserId(any(), any()))
                .thenReturn(Optional.of(existingCart));

        when(pricingService.getFinalPrice(any()))
                .thenReturn(BigDecimal.valueOf(950));

        when(cartRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = cartService.addToCart("SKU-1", 1);

        assertThat(cart.getItems()).hasSize(1);
        verify(cartRepository).save(existingCart);
    }

    @Test
    void addToCart_throwsExceptionWhenProductNotFound() {
        when(productRepository.findByTenantIdAndSku(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                cartService.addToCart("SKU-404", 1))
                .isInstanceOf(ProductNotFoundForCartException.class);
    }

    @Test
    void addToCart_throwsExceptionWhenInventoryInsufficient() {
        Product product = Product.builder()
                .inventory(1)
                .build();

        when(productRepository.findByTenantIdAndSku(any(), any()))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() ->
                cartService.addToCart("SKU-1", 5))
                .isInstanceOf(InsufficientInventoryException.class);
    }

    @Test
    void getActiveCart_returnsCart() {
        Cart cart = Cart.builder().build();

        when(cartRepository.findByTenantIdAndUserId(any(), any()))
                .thenReturn(Optional.of(cart));

        Cart result = cartService.getCart();

        assertThat(result).isNotNull();
    }

    @Test
    void getActiveCart_throwsExceptionWhenNotFound() {
        when(cartRepository.findByTenantIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart())
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void clearCart_disablesActiveCart() {
        Cart cart = Cart.builder()
                .build();

        when(cartRepository.findByTenantIdAndUserId(any(), any()))
                .thenReturn(Optional.of(cart));

        cartService.clearCart("user-1");

        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_throwsExceptionWhenNoActiveCart() {
        when(cartRepository.findByTenantIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                cartService.clearCart("user-1"))
                .isInstanceOf(CartNotFoundException.class);
    }
}
