package com.example.ecommerce;

import com.example.ecommerce.exception.tenant.TenantAlreadyExistsException;
import com.example.ecommerce.tenant.domain.Tenant;
import com.example.ecommerce.tenant.repository.TenantRepository;
import com.example.ecommerce.tenant.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @Captor
    private ArgumentCaptor<Tenant> tenantCaptor;

    @Test
    void createTenant_successfullyCreatesTenant() {
        when(tenantRepository.existsByNameIgnoreCase("Amazon")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant tenant = tenantService.createTenant("Amazon");

        assertThat(tenant.getName()).isEqualTo("Amazon");
        assertThat(tenant.getTenantSlug()).isEqualTo("amazon");
        assertThat(tenant.isActive()).isTrue();

        verify(tenantRepository).save(tenantCaptor.capture());
        Tenant savedTenant = tenantCaptor.getValue();

        assertThat(savedTenant.getName()).isEqualTo("Amazon");
        assertThat(savedTenant.getTenantSlug()).isEqualTo("amazon");
        assertThat(savedTenant.isActive()).isTrue();
    }

    @Test
    void createTenant_trimsNameAndGeneratesSlug() {
        when(tenantRepository.existsByNameIgnoreCase("Big Basket India")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant tenant = tenantService.createTenant("   Big Basket India   ");

        assertThat(tenant.getName()).isEqualTo("Big Basket India");
        assertThat(tenant.getTenantSlug()).isEqualTo("big_basket_india");
    }

    @Test
    void createTenant_throwsExceptionWhenTenantAlreadyExists() {
        when(tenantRepository.existsByNameIgnoreCase("Flipkart")).thenReturn(true);

        assertThatThrownBy(() -> tenantService.createTenant("Flipkart"))
                .isInstanceOf(TenantAlreadyExistsException.class)
                .hasMessageContaining("Flipkart");

        verify(tenantRepository, never()).save(any());
    }

    @Test
    void getAllTenants_returnsTenantList() {
        when(tenantRepository.findAll()).thenReturn(List.of(
                Tenant.builder().name("Amazon").build(),
                Tenant.builder().name("Flipkart").build()
        ));

        List<Tenant> tenants = tenantService.getAllTenants();

        assertThat(tenants).hasSize(2);
        verify(tenantRepository).findAll();
    }
}
