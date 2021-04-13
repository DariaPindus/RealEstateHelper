package com.daria.learn.rentalhelper.rentalfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RentalFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalFetcherApplication.class, args);
    }

}
