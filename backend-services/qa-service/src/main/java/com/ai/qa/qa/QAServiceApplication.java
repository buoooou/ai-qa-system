package com.ai.qa.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QAServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QAServiceApplication.class, args);
    }
}