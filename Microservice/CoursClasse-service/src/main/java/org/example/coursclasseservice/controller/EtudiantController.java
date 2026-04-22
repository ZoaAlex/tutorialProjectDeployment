package org.example.coursclasseservice.controller;

import org.example.coursclasseservice.dto.EtudiantDto;
import org.example.coursclasseservice.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;

    @Autowired
    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public ResponseEntity<List<EtudiantDto>> getAllEtudiants() {
        return ResponseEntity.ok(etudiantService.getAllEtudiants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDto> getEtudiantById(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getEtudiantById(id));
    }

    @PostMapping
    public ResponseEntity<EtudiantDto> createEtudiant(@RequestBody EtudiantDto etudiantDto) {
        return new ResponseEntity<>(etudiantService.createEtudiant(etudiantDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EtudiantDto> updateEtudiant(@PathVariable Long id, @RequestBody EtudiantDto etudiantDto) {
        return ResponseEntity.ok(etudiantService.updateEtudiant(id, etudiantDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }
}
