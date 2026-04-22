package org.example.coursclasseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class CoursClasseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoursClasseServiceApplication.class, args);
    }

}
