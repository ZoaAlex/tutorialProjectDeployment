package iusjc_planning.planning_service.controller;

import iusjc_planning.planning_service.dto.CreateDisponibiliteRequest;
import iusjc_planning.planning_service.dto.DisponibiliteEnseignantDTO;
import iusjc_planning.planning_service.model.JourSemaine;
import iusjc_planning.planning_service.service.DisponibiliteEnseignantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disponibilites")
@RequiredArgsConstructor
public class DisponibiliteEnseignantController {

    private final DisponibiliteEnseignantService disponibiliteService;

    @PostMapping
    public ResponseEntity<DisponibiliteEnseignantDTO> createDisponibilite(
            @Valid @RequestBody CreateDisponibiliteRequest request) {
        DisponibiliteEnseignantDTO disponibilite = disponibiliteService.createDisponibilite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(disponibilite);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibiliteEnseignantDTO> getDisponibiliteById(@PathVariable Long id) {
        DisponibiliteEnseignantDTO disponibilite = disponibiliteService.getDisponibiliteById(id);
        return ResponseEntity.ok(disponibilite);
    }

    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<List<DisponibiliteEnseignantDTO>> getDisponibilitesByEnseignant(
            @PathVariable Long enseignantId) {
        List<DisponibiliteEnseignantDTO> disponibilites = disponibiliteService
                .getDisponibilitesByEnseignant(enseignantId);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/enseignant/{enseignantId}/jour/{jour}")
    public ResponseEntity<List<DisponibiliteEnseignantDTO>> getDisponibilitesByEnseignantAndJour(
            @PathVariable Long enseignantId,
            @PathVariable JourSemaine jour) {
        List<DisponibiliteEnseignantDTO> disponibilites = disponibiliteService
                .getDisponibilitesByEnseignantAndJour(enseignantId, jour);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/enseignant/{enseignantId}/actives")
    public ResponseEntity<List<DisponibiliteEnseignantDTO>> getDisponibilitesActives(@PathVariable Long enseignantId) {
        List<DisponibiliteEnseignantDTO> disponibilites = disponibiliteService.getDisponibilitesActives(enseignantId);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/enseignant/{enseignantId}/check")
    public ResponseEntity<Map<String, Boolean>> checkDisponibilite(
            @PathVariable Long enseignantId,
            @RequestParam JourSemaine jour,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime heure) {

        boolean disponible = disponibiliteService.isEnseignantDisponible(enseignantId, jour, heure);
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    @GetMapping("/enseignant/{enseignantId}/check-creneau")
    public ResponseEntity<Map<String, Boolean>> checkDisponibiliteCreneau(
            @PathVariable Long enseignantId,
            @RequestParam JourSemaine jour,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime heureDebut,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime heureFin) {

        boolean disponible = disponibiliteService.isEnseignantDisponiblePourCreneau(
                enseignantId, jour, heureDebut, heureFin);
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibiliteEnseignantDTO> updateDisponibilite(
            @PathVariable Long id,
            @Valid @RequestBody CreateDisponibiliteRequest request) {
        DisponibiliteEnseignantDTO disponibilite = disponibiliteService.updateDisponibilite(id, request);
        return ResponseEntity.ok(disponibilite);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<DisponibiliteEnseignantDTO> toggleDisponibilite(@PathVariable Long id) {
        DisponibiliteEnseignantDTO disponibilite = disponibiliteService.toggleDisponibilite(id);
        return ResponseEntity.ok(disponibilite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id) {
        disponibiliteService.deleteDisponibilite(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/enseignant/{enseignantId}")
    public ResponseEntity<Void> deleteAllDisponibilitesByEnseignant(@PathVariable Long enseignantId) {
        disponibiliteService.deleteAllDisponibilitesByEnseignant(enseignantId);
        return ResponseEntity.noContent().build();
    }
}