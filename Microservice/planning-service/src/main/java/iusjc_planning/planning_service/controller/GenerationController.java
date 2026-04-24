package iusjc_planning.planning_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iusjc_planning.planning_service.dto.UpdateEmploiDuTempsRequest;
import iusjc_planning.planning_service.exception.ResourceNotFoundException;
import iusjc_planning.planning_service.generation.ResultatGeneration;
import iusjc_planning.planning_service.model.EmploiDuTemps;
import iusjc_planning.planning_service.service.PlanningService;

@RestController
@RequestMapping("/api/generation")
public class GenerationController {

    private static final Logger log = LoggerFactory.getLogger(GenerationController.class);

    private final PlanningService planningService;

    public GenerationController(PlanningService planningService) {
        this.planningService = planningService;
    }

    /** Lance la génération globale de l'emploi du temps. */
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

    /** Récupère les emplois du temps persistés en base, filtrés optionnellement par classeId. */
    @GetMapping
    public ResponseEntity<List<EmploiDuTemps>> getEmploiDuTemps(
            @RequestParam(required = false) Long classeId) {
        return ResponseEntity.ok(planningService.getEmploiDuTemps(classeId));
    }

    /** Modifie manuellement un créneau (jour, heure, salle, enseignant). */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmploiDuTemps(
            @PathVariable Long id,
            @RequestBody UpdateEmploiDuTempsRequest request) {
        try {
            EmploiDuTemps updated = planningService.updateEmploiDuTemps(id, request);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur lors de la modification du créneau {} : {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de la modification : " + e.getMessage());
        }
    }

    /** Supprime un créneau de l'emploi du temps. */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmploiDuTemps(@PathVariable Long id) {
        try {
            planningService.deleteEmploiDuTemps(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
