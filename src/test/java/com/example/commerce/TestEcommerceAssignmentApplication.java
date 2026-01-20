package com.example.commerce;

import org.springframework.boot.SpringApplication;

public class TestEcommerceAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.from(EcommerceAssignmentApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
