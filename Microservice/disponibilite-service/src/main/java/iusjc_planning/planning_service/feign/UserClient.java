package iusjc_planning.planning_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import iusjc_planning.planning_service.dto.EnseignantResponse;

@FeignClient(name = "user-service", url = "${services.user.url:http://user-service:8081}")
public interface UserClient {

    @GetMapping("/api/enseignants/{id}")
    EnseignantResponse getEnseignantById(@PathVariable("id") Long id);
}
