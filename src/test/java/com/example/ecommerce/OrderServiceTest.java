package com.example.ecommerce;

import com.example.ecommerce.cart.domain.Cart;
import com.example.ecommerce.cart.domain.CartItem;
import com.example.ecommerce.cart.service.CartService;
import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.domain.OrderStatus;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.order.service.OrderService;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.security.util.SecurityUtil;
import com.example.ecommerce.tenant.context.TenantContext;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private AutoCloseable securityUtilMock;

    @BeforeEach
    void setup() {
        TenantContext.setTenantId("tenant-1");
        securityUtilMock = Mockito.mockStatic(SecurityUtil.class);
        ((MockedStatic<?>) securityUtilMock).when(SecurityUtil::getCurrentUserId)
                .thenReturn("user-1");
    }

    @AfterEach
    void cleanup() throws Exception {
        TenantContext.clear();
        securityUtilMock.close();
    }

    @Test
    void placeOrder_successfullyPlacesOrder() {
        String idempotencyKey = "key-123";

        when(orderRepository.findByTenantIdAndIdempotencyKey("tenant-1", idempotencyKey))
                .thenReturn(Optional.empty());

        CartItem item = CartItem.builder()
                .productId("p1")
                .sku("SKU-1")
                .name("Test Product")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50))
                .build();
        Cart cart = Cart.builder().items(List.of(item)).build();

        when(cartService.getActiveCart()).thenReturn(cart);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(1L);
        when(productRepository.decrementInventory("p1", 2)).thenReturn(updateResult);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(cartService).clearCart("user-1");

        Order order = orderService.placeOrder(idempotencyKey);

        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));

        verify(orderRepository).save(order);
        verify(cartService).clearCart("user-1");
    }

    @Test
    void placeOrder_returnsExistingOrderIfIdempotencyKeyExists() {
        String idempotencyKey = "key-123";
        Order existingOrder = Order.builder().idempotencyKey(idempotencyKey).build();

        when(orderRepository.findByTenantIdAndIdempotencyKey("tenant-1", idempotencyKey))
                .thenReturn(Optional.of(existingOrder));

        Order order = orderService.placeOrder(idempotencyKey);

        assertThat(order).isEqualTo(existingOrder);
        verify(orderRepository, never()).save(any());
        verify(cartService, never()).getActiveCart();
    }

    @Test
    void placeOrder_throwsExceptionWhenCartIsEmpty() {
        String idempotencyKey = "key-123";
        Cart emptyCart = Cart.builder().items(List.of()).build();

        when(orderRepository.findByTenantIdAndIdempotencyKey(any(), any()))
                .thenReturn(Optional.empty());

        when(cartService.getActiveCart()).thenReturn(emptyCart);

        assertThatThrownBy(() -> orderService.placeOrder(idempotencyKey))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    void placeOrder_throwsExceptionWhenInventoryInsufficient() {
        String idempotencyKey = "key-123";

        when(orderRepository.findByTenantIdAndIdempotencyKey(any(), any()))
                .thenReturn(Optional.empty());

        CartItem item = CartItem.builder()
                .productId("p1")
                .sku("SKU-1")
                .name("Test Product")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50))
                .build();
        Cart cart = Cart.builder().items(List.of(item)).build();

        when(cartService.getActiveCart()).thenReturn(cart);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(0L);
        when(productRepository.decrementInventory("p1", 2)).thenReturn(updateResult);

        assertThatThrownBy(() -> orderService.placeOrder(idempotencyKey))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");

        verify(orderRepository, never()).save(any());
        verify(cartService, never()).clearCart(any());
    }
}
