package iusjc_planning.salles_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Application principale du microservice de gestion des salles et du matériel
 * IUSJC Planning System
 * 
 * @author IUSJC Planning Team
 * @version 1.0
 */
@SpringBootApplication
@EnableFeignClients
public class SallesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SallesServiceApplication.class, args);
    }

}