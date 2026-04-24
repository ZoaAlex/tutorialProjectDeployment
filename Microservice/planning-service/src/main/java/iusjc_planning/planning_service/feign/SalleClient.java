package iusjc_planning.planning_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import iusjc_planning.planning_service.dto.SalleClientDTO;

/**
 * Client Feign vers salles-service.
 * Récupère les salles disponibles pour la génération.
 */
@FeignClient(name = "salles-service", url = "${services.salle.url:http://salle-service:8084}")
public interface SalleClient {

    /**
     * Récupère toutes les salles avec leur capacité.
     * salles-service doit exposer cet endpoint.
     */
    @GetMapping("/api/salles")
    List<SalleClientDTO> getAllSalles();
}
