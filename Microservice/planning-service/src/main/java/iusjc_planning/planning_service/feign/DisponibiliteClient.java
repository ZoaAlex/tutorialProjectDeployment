package iusjc_planning.planning_service.feign;

import iusjc_planning.planning_service.dto.DisponibiliteEnseignantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "disponibilite-service",url = "http://localhost:8083")
public interface DisponibiliteClient {

    @GetMapping("/api/disponibilites/enseignant/{enseignantId}/actives")
    List<DisponibiliteEnseignantDTO> getDisponibilitesActives(@PathVariable("enseignantId") Long enseignantId);
}
