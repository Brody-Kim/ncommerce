package com.rogistudio.ncommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class RogicommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RogicommerceApplication.class, args);
    }

}
