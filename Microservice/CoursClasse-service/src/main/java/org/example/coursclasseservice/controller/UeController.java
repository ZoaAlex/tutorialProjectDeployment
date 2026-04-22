package org.example.coursclasseservice.controller;

import java.util.List;

import org.example.coursclasseservice.dto.UeDto;
import org.example.coursclasseservice.service.UeService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/ues")
public class UeController {

    private final UeService ueService;

    @Autowired
    public UeController(UeService ueService) {
        this.ueService = ueService;
    }

    @GetMapping
    public ResponseEntity<List<UeDto>> getAllUes() {
        return ResponseEntity.ok(ueService.getAllUes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UeDto> getUeById(@PathVariable Long id) {
        return ResponseEntity.ok(ueService.getUeById(id));
    }

    @PostMapping
    public ResponseEntity<UeDto> createUe(@RequestBody UeDto ueDto) {
        return  ResponseEntity.ok(ueService.createUe(ueDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UeDto> updateUe(@PathVariable Long id, @RequestBody UeDto ueDto) {
        return ResponseEntity.ok(ueService.updateUe(id, ueDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUe(@PathVariable Long id) {
        ueService.deleteUe(id);
        return ResponseEntity.noContent().build();
    }
}
