package iusjc_planning.planning_service.feign;

import iusjc_planning.planning_service.dto.SalleClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Client Feign vers salles-service.
 * Récupère les salles disponibles pour la génération.
 */
@FeignClient(name = "salles-service", url = "http://localhost:8084")
public interface SalleClient {

    /**
     * Récupère toutes les salles avec leur capacité.
     * salles-service doit exposer cet endpoint.
     */
    @GetMapping("/api/salles")
    List<SalleClientDTO> getAllSalles();
}
