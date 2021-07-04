package com.ccc.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomersApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomersApplication.class, args);
	}
}
