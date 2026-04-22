package org.example.coursclasseservice.controller;

import java.util.List;

import org.example.coursclasseservice.dto.FiliereDto;
import org.example.coursclasseservice.service.FiliereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/filieres")
public class FiliereController {

    private final FiliereService filiereService;

    @Autowired
    public FiliereController(FiliereService filiereService) {
        this.filiereService = filiereService;
    }

    @GetMapping
    public ResponseEntity<List<FiliereDto>> getAllFilieres() {
        return ResponseEntity.ok(filiereService.getAllFilieres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FiliereDto> getFiliereById(@PathVariable Long id) {
        return ResponseEntity.ok(filiereService.getFiliereById(id));
    }

    @PostMapping
    public ResponseEntity<FiliereDto> createFiliere(@RequestBody FiliereDto filiereDto) {
        return new ResponseEntity<>(filiereService.createFiliere(filiereDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FiliereDto> updateFiliere(@PathVariable Long id, @RequestBody FiliereDto filiereDto) {
        return ResponseEntity.ok(filiereService.updateFiliere(id, filiereDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiliere(@PathVariable Long id) {
        filiereService.deleteFiliere(id);
        return ResponseEntity.noContent().build();
    }
}
