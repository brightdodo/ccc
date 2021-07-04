package com.ccc.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

}

@RestController
@Slf4j
class ProductsController{
    @GetMapping("/products")
    Flux<Product> getProducts(){
        log.info("Fetching product catalog");
        return Flux.just("iPhone", "MacBook", "Tablet", "KeyBoard")
            .map(name -> new Product(ThreadLocalRandom.current().nextInt(1, 9 + 1), name));
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Product{
    private Integer id;
    private String name;
}