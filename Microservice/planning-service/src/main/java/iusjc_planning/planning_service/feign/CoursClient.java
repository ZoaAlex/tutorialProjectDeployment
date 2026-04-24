package iusjc_planning.planning_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import iusjc_planning.planning_service.dto.CoursClientDTO;

/**
 * Client Feign vers coursclasse-service.
 * Récupère les cours à planifier et met à jour nbreheurefait après placement.
 */
@FeignClient(name = "coursclasse-service", url = "${services.coursclasse.url:http://coursclasse-service:8085}")
public interface CoursClient {

    @GetMapping("/api/cours/a-planifier")
    List<CoursClientDTO> getCoursAPlanifier();

    @PutMapping("/api/cours/{id}/heures-effectuees")
    void mettreAJourHeuresEffectuees(@PathVariable("id") Long coursId,
                                     @RequestParam("heures") int heures);
}
