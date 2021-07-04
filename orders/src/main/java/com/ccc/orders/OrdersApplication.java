package com.ccc.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}

    @Bean
    @LoadBalanced
    WebClient http(WebClient.Builder builder) {
        return builder.build();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Order{
    private Integer id;
    private UUID customerId;
    private List<Product> products;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Product{
    private Integer id;
    private String name;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Customer {
    private UUID id;
    private String name;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CustomerOrder{
    private Customer customer;
    private List<Order> orders;
}

@Component
@RequiredArgsConstructor
class CrmClient {
    private final WebClient http;

    Flux<Product> getProducts(){
        return http.get()
            .uri("lb://product-service/products")
            .retrieve()
            .bodyToFlux(Product.class);
    }

    Flux<Customer> getCustomers(){
        return http.get()
            .uri("http://customer-service/customers")
            .retrieve()
            .bodyToFlux(Customer.class);
    }

    List<Order> randomOrdersFor(UUID customerId){
        var products = getProducts().collectList().block();
        var orderList = new ArrayList<Order>();
        var max = ThreadLocalRandom.current().nextInt(1, 9 + 1);
        for (int i = 1; i <= max; i++) {
            orderList.add(new Order(i, customerId, products));
        }
        return orderList;
    }

    Flux<CustomerOrder> getCustomerOrders(){
        return this.getCustomers()
            .flatMap(customer -> Mono.zip(Mono.just(customer), Mono.just(randomOrdersFor(customer.getId()))))
            .map(tuple -> new CustomerOrder(tuple.getT1(), tuple.getT2()));
    }
}

@RestController
@RequiredArgsConstructor
class OrdersController {
    private final CrmClient crmClient;

    @GetMapping("/orders/customer")
    Flux<CustomerOrder> getCustomerOrders(){
        return crmClient.getCustomerOrders();
    }
}
