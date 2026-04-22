package org.example.coursclasseservice.controller;

import org.example.coursclasseservice.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/import")
public class ImportController {
    public final EcoleService ecoleService;
    public final ClasseService classeService;
    public final FiliereService filiereService;
    public final UeService ueService;
    public final EtudiantService etudiantService;
    public final GroupeEtudiantService groupeEtudiantService;
    public final CoursService coursService;

    public ImportController(EcoleService ecoleService, ClasseService classeService, FiliereService filiereService,
            UeService ueService, EtudiantService etudiantService, GroupeEtudiantService groupeEtudiantService,
            CoursService coursService) {
        this.ecoleService = ecoleService;
        this.classeService = classeService;
        this.filiereService = filiereService;
        this.ueService = ueService;
        this.etudiantService = etudiantService;
        this.groupeEtudiantService = groupeEtudiantService;
        this.coursService = coursService;
    }

    @PostMapping("/{entityType}")
    public ResponseEntity<?> importCours(@PathVariable String entityType, @RequestParam("file") MultipartFile file)
            throws IOException {
        return switch (entityType) {
            case "classes" -> classeService.importerClasse(file);
            case "filieres" -> filiereService.importerFiliere(file);
            case "ue" -> ueService.importerUe(file);
            case "etudiant" -> etudiantService.importerEtudiant(file);
            case "groupe" -> groupeEtudiantService.importerGroupe(file);
            case "ecoles" -> ecoleService.importerEcole(file);
            case "cours" -> coursService.importerCours(file);
            default -> ResponseEntity.badRequest().build();
        };
    }
}
