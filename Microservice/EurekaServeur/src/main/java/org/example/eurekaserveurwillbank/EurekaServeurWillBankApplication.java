package org.example.eurekaserveurwillbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServeurWillBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServeurWillBankApplication.class, args);
    }

}
