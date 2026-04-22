package iusjc_planning.planning_service.feign;

import iusjc_planning.planning_service.dto.ClasseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "coursclasse-service", url = "http://localhost:8085")
public interface ClasseClient {

    @GetMapping("/api/classes")
    List<ClasseResponse> getAllClasses();

    @GetMapping("/api/classes/{id}")
    ClasseResponse getClasseById(@PathVariable("id") Long id);
}
