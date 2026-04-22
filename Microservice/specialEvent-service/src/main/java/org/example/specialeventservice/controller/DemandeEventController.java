package org.example.specialeventservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.specialeventservice.dto.DemandeEventDTO;
import org.example.specialeventservice.model.Enum.StatutDemande;
import org.example.specialeventservice.service.DemandeEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeEventController {

    private final DemandeEventService demandeService;

    @PostMapping
    public ResponseEntity<DemandeEventDTO> createDemande(@RequestBody DemandeEventDTO dto) {
        return ResponseEntity.ok(demandeService.createDemande(dto));
    }

    @GetMapping
    public ResponseEntity<List<DemandeEventDTO>> getAllDemandes() {
        return ResponseEntity.ok(demandeService.getAllDemandes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeEventDTO> getDemande(@PathVariable Long id) {
        return ResponseEntity.ok(demandeService.getDemandeById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DemandeEventDTO> updateStatus(@PathVariable Long id, @RequestParam StatutDemande status) {
        return ResponseEntity.ok(demandeService.updateStatut(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        demandeService.deleteDemande(id);
        return ResponseEntity.noContent().build();
    }
}
