package com.ccc.customers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import reactor.core.publisher.Flux;

@RestController
public class CustomerController {

    @GetMapping("customers")
    public Flux<Customer> getCustomers(){
        return Flux.just("Bright", "Calvin", "Tidza")
            .map(name -> new Customer(UUID.randomUUID(), name));
    }
}
