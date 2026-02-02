package com.example.ecommerce.common.config;

import com.example.ecommerce.common.filter.RateLimitFilter;
import com.example.ecommerce.common.rateLimit.RateLimitService;
import com.example.ecommerce.common.filter.RequestLoggingFilter;
import com.example.ecommerce.tenant.filter.TenantFilter;
import com.example.ecommerce.tenant.repository.TenantRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public TenantFilter tenantFilter(TenantRepository tenantRepository) {
        return new TenantFilter(tenantRepository);
    }

    @Bean
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    @Bean
    public RateLimitFilter rateLimitFilter(RateLimitService rateLimitService) {
        return new RateLimitFilter(rateLimitService);
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilterRegistration(RequestLoggingFilter filter) {
        FilterRegistrationBean<RequestLoggingFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilterRegistration(TenantFilter filter) {
        FilterRegistrationBean<TenantFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(2);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(3);

        // limit only specific API patterns
        // reg.addUrlPatterns("/api/v1/orders/*", "/api/v1/products/*");

        return reg;
    }
}