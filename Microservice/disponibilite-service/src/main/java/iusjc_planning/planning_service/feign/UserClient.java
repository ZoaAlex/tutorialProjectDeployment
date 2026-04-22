package iusjc_planning.planning_service.feign;

import iusjc_planning.planning_service.dto.EnseignantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {

    @GetMapping("/api/enseignants/{id}")
    EnseignantResponse getEnseignantById(@PathVariable("id") Long id);
}
