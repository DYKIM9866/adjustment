package com.sparta.adjustment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class AdjustmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdjustmentApplication.class, args);
    }

}
