package org.example.specialeventservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.specialeventservice.dto.SpecialEventDTO;
import org.example.specialeventservice.service.SpecialEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SpecialEventController {

    private final SpecialEventService eventService;

    @PostMapping("/from-demande/{demandeId}")
    public ResponseEntity<SpecialEventDTO> createFromDemande(@PathVariable Long demandeId, @RequestParam String type) {
        return ResponseEntity.ok(eventService.createEventFromDemande(demandeId, type));
    }

    @GetMapping
    public ResponseEntity<List<SpecialEventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialEventDTO> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialEventDTO> updateEvent(@PathVariable Long id, @RequestBody SpecialEventDTO dto) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
