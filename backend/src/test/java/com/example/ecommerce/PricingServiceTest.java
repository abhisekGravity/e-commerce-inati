package com.example.ecommerce;

import com.example.ecommerce.pricing.dto.PricingContext;
import com.example.ecommerce.pricing.engine.PricingEngine;
import com.example.ecommerce.pricing.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private PricingEngine pricingEngine;

    @InjectMocks
    private PricingService pricingService;

    private PricingContext context;

    @BeforeEach
    void setup() {
        context = PricingContext.builder()
                .tenantId("tenant-1")
                .sku("SKU-1")
                .basePrice(BigDecimal.valueOf(100))
                .inventory(10)
                .build();
    }

    @Test
    void getFinalPrice_delegatesToPricingEngine() {
        when(pricingEngine.calculatePrice(context)).thenReturn(BigDecimal.valueOf(90));

        BigDecimal finalPrice = pricingService.getFinalPrice(context);

        assertThat(finalPrice).isEqualByComparingTo("90");
        verify(pricingEngine).calculatePrice(context);
    }

    @Test
    void getFinalPrice_returnsCorrectValue() {
        when(pricingEngine.calculatePrice(any(PricingContext.class)))
                .thenReturn(BigDecimal.valueOf(85));

        BigDecimal finalPrice = pricingService.getFinalPrice(context);

        assertThat(finalPrice).isEqualByComparingTo("85");
        verify(pricingEngine).calculatePrice(context);
    }
}
