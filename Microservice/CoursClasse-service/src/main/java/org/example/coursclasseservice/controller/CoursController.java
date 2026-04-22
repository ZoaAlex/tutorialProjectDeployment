package org.example.coursclasseservice.controller;

import org.example.coursclasseservice.dto.CoursDto;
import org.example.coursclasseservice.dto.SalleAttributCoursdto;
import org.example.coursclasseservice.service.CoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
public class CoursController {

    private final CoursService coursService;

    @Autowired
    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    @GetMapping
    public ResponseEntity<List<CoursDto>> getAllCours() {
        return ResponseEntity.ok(coursService.getAllCours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoursDto> getCoursById(@PathVariable Long id) {
        return ResponseEntity.ok(coursService.getCoursById(id));
    }

    @PostMapping
    public ResponseEntity<CoursDto> createCours(@RequestBody CoursDto coursDto) {
        return  ResponseEntity.ok(coursService.createCours(coursDto));
    }

    @PutMapping("/update")
    public ResponseEntity<CoursDto> updateCours( @RequestBody CoursDto coursDto) {
        return ResponseEntity.ok(coursService.updateCours( coursDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retourne les cours dont le volume horaire n'est pas encore atteint.
     * Appelé par planning-service via Feign avant la génération.
     */
    @GetMapping("/a-planifier")
    public ResponseEntity<List<CoursDto>> getCoursAPlanifier() {
        return ResponseEntity.ok(coursService.getCoursAPlanifier());
    }

    /**
     * Incrémente nbreheurefait après un placement dans l'emploi du temps.
     * Appelé par planning-service via Feign après chaque génération.
     */
    @PutMapping("/{id}/heures-effectuees")
    public ResponseEntity<CoursDto> mettreAJourHeuresEffectuees(
            @PathVariable Long id,
            @RequestParam int heures) {
        return ResponseEntity.ok(coursService.mettreAJourHeuresEffectuees(id, heures));
    }
}
