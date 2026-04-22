package org.example.coursclasseservice.controller;

import org.example.coursclasseservice.dto.EcoleDto;
import org.example.coursclasseservice.service.EcoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecoles")
public class EcoleController {

    private final EcoleService ecoleService;

    @Autowired
    public EcoleController(EcoleService ecoleService) {
        this.ecoleService = ecoleService;
    }

    @GetMapping
    public ResponseEntity<List<EcoleDto>> getAllEcoles() {
        return ResponseEntity.ok(ecoleService.getAllEcoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EcoleDto> getEcoleById(@PathVariable Long id) {
        return ResponseEntity.ok(ecoleService.getEcoleById(id));
    }

    @PostMapping
    public ResponseEntity<EcoleDto> createEcole(@RequestBody EcoleDto ecoleDto) {
        return new ResponseEntity<>(ecoleService.createEcole(ecoleDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EcoleDto> updateEcole(@PathVariable Long id, @RequestBody EcoleDto ecoleDto) {
        return ResponseEntity.ok(ecoleService.updateEcole(id, ecoleDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEcole(@PathVariable Long id) {
        ecoleService.deleteEcole(id);
        return ResponseEntity.noContent().build();
    }

}
