package iusjc_planning.planning_service.feign;

import iusjc_planning.planning_service.dto.CoursClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Client Feign vers coursclasse-service.
 * Récupère les cours à planifier et met à jour nbreheurefait après placement.
 */
@FeignClient(name = "coursclasse-service", url = "http://localhost:8085")
public interface CoursClient {

    /**
     * Récupère tous les cours dont le volume restant est > 0.
     * coursclasse-service doit exposer cet endpoint.
     */
    @GetMapping("/api/cours/a-planifier")
    List<CoursClientDTO> getCoursAPlanifier();

    /**
     * Met à jour le nombre d'heures effectuées d'un cours après placement.
     */
    @PutMapping("/api/cours/{id}/heures-effectuees")
    void mettreAJourHeuresEffectuees(@PathVariable("id") Long coursId,
                                     @RequestParam("heures") int heures);
}
