package org.example.coursclasseservice.controller;

import java.util.List;

import org.example.coursclasseservice.dto.ClasseDto;
import org.example.coursclasseservice.service.ClasseService;
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
@RequestMapping("/api/classes")
public class ClasseController {

    private final ClasseService classeService;

    @Autowired
    public ClasseController(ClasseService classeService) {
        this.classeService = classeService;
    }

    @GetMapping
    public ResponseEntity<List<ClasseDto>> getAllClasses() {
        return ResponseEntity.ok(classeService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseDto> getClasseById(@PathVariable Long id) {
        return ResponseEntity.ok(classeService.getClasseById(id));
    }

    @PostMapping
    public ResponseEntity<ClasseDto> createClasse(@RequestBody ClasseDto classeDto) {
        return new ResponseEntity<>(classeService.createClasse(classeDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClasseDto> updateClasse(@PathVariable Long id, @RequestBody ClasseDto classeDto) {
        return ResponseEntity.ok(classeService.updateClasse(id, classeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        classeService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }
}
