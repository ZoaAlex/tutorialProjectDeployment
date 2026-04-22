package org.example.coursclasseservice.feign;

import org.example.coursclasseservice.dto.SalleAttributCoursdto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "salles-service",
        url = "http://localhost:8084"
)
public interface SalleFeignClient {

    @GetMapping("/api/salles/{id}")
    SalleAttributCoursdto getSalleById(@PathVariable("id") Long id);
}
