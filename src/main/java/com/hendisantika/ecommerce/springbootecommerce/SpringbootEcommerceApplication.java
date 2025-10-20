package com.hendisantika.ecommerce.springbootecommerce;

import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import com.hendisantika.ecommerce.springbootecommerce.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootEcommerceApplication.class, args);
    }

    // Commented out - using data.sql for sample data instead
    // @Bean
    // CommandLineRunner runner(ProductService productService) {
    //     return args -> {
    //         // Sample data initialization moved to src/main/resources/data.sql
    //     };
    // }

}
