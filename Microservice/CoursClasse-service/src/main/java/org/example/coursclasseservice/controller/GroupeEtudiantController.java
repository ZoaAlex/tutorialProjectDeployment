package org.example.coursclasseservice.controller;

import org.example.coursclasseservice.dto.GroupeEtudiantDto;
import org.example.coursclasseservice.service.GroupeEtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupes-etudiants")
public class GroupeEtudiantController {

    private final GroupeEtudiantService groupeEtudiantService;

    @Autowired
    public GroupeEtudiantController(GroupeEtudiantService groupeEtudiantService) {
        this.groupeEtudiantService = groupeEtudiantService;
    }

    @GetMapping
    public ResponseEntity<List<GroupeEtudiantDto>> getAllGroupes() {
        return ResponseEntity.ok(groupeEtudiantService.getAllGroupes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupeEtudiantDto> getGroupeById(@PathVariable Long id) {
        return ResponseEntity.ok(groupeEtudiantService.getGroupeById(id));
    }

    @PostMapping
    public ResponseEntity<GroupeEtudiantDto> createGroupe(@RequestBody GroupeEtudiantDto groupeDto) {
        return new ResponseEntity<>(groupeEtudiantService.createGroupe(groupeDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupeEtudiantDto> updateGroupe(@PathVariable Long id,
            @RequestBody GroupeEtudiantDto groupeDto) {
        return ResponseEntity.ok(groupeEtudiantService.updateGroupe(id, groupeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroupe(@PathVariable Long id) {
        groupeEtudiantService.deleteGroupe(id);
        return ResponseEntity.noContent().build();
    }
}
