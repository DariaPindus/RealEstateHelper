package com.daria.learn.rentalhelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class RentalHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalHelperApplication.class, args);
    }

}
