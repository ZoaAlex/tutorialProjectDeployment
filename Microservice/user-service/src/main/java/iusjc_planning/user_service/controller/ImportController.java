package iusjc_planning.user_service.controller;

import iusjc_planning.user_service.service.UserImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/import")
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private final UserImportService userImportService;

    /**
     * POST /api/users/import
     * Importe des utilisateurs depuis un fichier Excel.
     *
     * Format colonnes (ligne 1 = en-tête) :
     * nom | prenom | email | password | role | specialite | grade
     */
    @PostMapping
    public ResponseEntity<?> importerUtilisateurs(@RequestParam("file") MultipartFile file) {
        log.info("Import Excel utilisateurs — fichier : {}, taille : {} octets",
                file.getOriginalFilename(), file.getSize());
        return userImportService.importerUtilisateurs(file);
    }
}
