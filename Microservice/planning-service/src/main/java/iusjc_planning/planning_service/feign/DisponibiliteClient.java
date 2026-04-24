package iusjc_planning.planning_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import iusjc_planning.planning_service.dto.DisponibiliteEnseignantDTO;

@FeignClient(name = "disponibilite-service", url = "${services.disponibilite.url:http://disponibilite-service:8083}")
public interface DisponibiliteClient {

    @GetMapping("/api/disponibilites/enseignant/{enseignantId}/actives")
    List<DisponibiliteEnseignantDTO> getDisponibilitesActives(@PathVariable("enseignantId") Long enseignantId);
}
