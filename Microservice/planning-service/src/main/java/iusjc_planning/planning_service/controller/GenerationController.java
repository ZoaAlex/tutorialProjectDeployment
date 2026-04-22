package iusjc_planning.planning_service.controller;

import iusjc_planning.planning_service.model.EmploiDuTemps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import iusjc_planning.planning_service.generation.ResultatGeneration;
import iusjc_planning.planning_service.service.PlanningService;

import java.util.List;

/**
 * Endpoint de déclenchement de la génération de l'emploi du temps.
 */
@RestController
@RequestMapping("/api/generation")
public class GenerationController {

    private static final Logger log = LoggerFactory.getLogger(GenerationController.class);

    private final PlanningService planningService;

    public GenerationController(PlanningService planningService) {
        this.planningService = planningService;
    }

    /**
     * Lance la génération globale de l'emploi du temps.
     */
    @PostMapping
    public ResponseEntity<?> generer() {
        log.info("Requête de génération globale reçue.");
        try {
            ResultatGeneration resultat = planningService.genererTout();
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            log.error("Erreur lors de la génération : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Une erreur est survenue lors de la génération : " + e.getMessage());
        }
    }

    /**
     * Récupère les emplois du temps persistés en base.
     * @param classeId (optionnel) filtre par classe
     */
    @GetMapping
    public ResponseEntity<List<EmploiDuTemps>> getEmploiDuTemps(
            @RequestParam(required = false) Long classeId) {
        return ResponseEntity.ok(planningService.getEmploiDuTemps(classeId));
    }
}
