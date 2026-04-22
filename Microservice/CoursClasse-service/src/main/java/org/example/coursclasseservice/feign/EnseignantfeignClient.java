package org.example.coursclasseservice.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url="http://localhost:8081"
)
public interface EnseignantfeignClient {

    @GetMapping("/api/users/exists")
    boolean existEmail(@RequestParam String email);


}
