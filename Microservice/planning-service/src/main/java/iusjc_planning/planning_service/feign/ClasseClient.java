package iusjc_planning.planning_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import iusjc_planning.planning_service.dto.ClasseResponse;

@FeignClient(name = "coursclasse-service-classes", url = "${services.coursclasse.url:http://coursclasse-service:8085}")
public interface ClasseClient {

    @GetMapping("/api/classes")
    List<ClasseResponse> getAllClasses();

    @GetMapping("/api/classes/{id}")
    ClasseResponse getClasseById(@PathVariable("id") Long id);
}
