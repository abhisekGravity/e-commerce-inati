package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class EcommerceAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceAssignmentApplication.class, args);
	}

}
