package com.example.ecommerce.common.config;

import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI springShopOpenAPI() {
		return new OpenAPI().info(new Info().title("Multi tenant E-commerce project")
			.description("Backend APIs for Multi tenant E-commerce project")
			.version("v1.0.0")
			.contact(new Contact().name("Abhisek Nayak").url("http://port-folio-iota-seven.vercel.app/").email("abhiseknayak84@gmail.com"))
			.license(new License().name("License").url("/")))
			.externalDocs(new ExternalDocumentation().description("Multi tenant E-commerce project API Documentation")
			.url("http://localhost:8080/swagger-ui/index.html"));
	}
	
}