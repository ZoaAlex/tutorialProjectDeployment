package iusjc_planning.salles_service.controller;

import iusjc_planning.salles_service.dto.CreateSalleRequest;
import iusjc_planning.salles_service.dto.SalleDTO;
import iusjc_planning.salles_service.dto.SalleSearchCriteria;
import iusjc_planning.salles_service.model.StatutSalle;
import iusjc_planning.salles_service.service.SalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des salles
 */
@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
@Slf4j
public class SalleController {

    private final SalleService salleService;

    /**
     * Créer une nouvelle salle
     */
    @PostMapping
    public ResponseEntity<SalleDTO> creerSalle(
            @Valid @RequestBody CreateSalleRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de création de salle: {}", request.getCodeSalle());
        SalleDTO salle = salleService.creerSalle(request, utilisateur);
        return new ResponseEntity<>(salle, HttpStatus.CREATED);
    }

    /**
     * Récupérer toutes les salles
     */
    @GetMapping
    public ResponseEntity<List<SalleDTO>> getAllSalles() {
        log.debug("Demande de récupération de toutes les salles");
        List<SalleDTO> salles = salleService.getAllSalles();
        return ResponseEntity.ok(salles);
    }

    /**
     * Récupérer une salle par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalleDTO> getSalleById(@PathVariable Long id) {
        log.debug("Demande de récupération de la salle: {}", id);
        SalleDTO salle = salleService.getSalleById(id);
        return ResponseEntity.ok(salle);
    }

    /**
     * Récupérer une salle par son code
     */
    @GetMapping("/code/{codeSalle}")
    public ResponseEntity<SalleDTO> getSalleByCode(@PathVariable String codeSalle) {
        log.debug("Demande de récupération de la salle avec le code: {}", codeSalle);
        SalleDTO salle = salleService.getSalleByCode(codeSalle);
        return ResponseEntity.ok(salle);
    }

    /**
     * Rechercher des salles selon des critères
     */
    @PostMapping("/recherche")
    public ResponseEntity<List<SalleDTO>> rechercherSalles(@RequestBody SalleSearchCriteria criteria) {
        log.debug("Demande de recherche de salles avec critères");
        List<SalleDTO> salles = salleService.rechercherSalles(criteria);
        return ResponseEntity.ok(salles);
    }

    /**
     * Récupérer les salles avec capacité suffisante et disponibles
     */
    @GetMapping("/disponibles/capacite")
    public ResponseEntity<List<SalleDTO>> getSallesAvecCapaciteDisponibles(
            @RequestParam Integer capaciteRequise,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        log.debug("Demande de salles avec capacité >= {} disponibles du {} au {}",
                capaciteRequise, dateDebut, dateFin);
        List<SalleDTO> salles = salleService.getSallesAvecCapaciteDisponibles(capaciteRequise, dateDebut, dateFin);
        return ResponseEntity.ok(salles);
    }

    /**
     * Mettre à jour une salle
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalleDTO> mettreAJourSalle(
            @PathVariable Long id,
            @Valid @RequestBody SalleDTO salleDTO,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de mise à jour de la salle: {}", id);
        SalleDTO salle = salleService.mettreAJourSalle(id, salleDTO, utilisateur);
        return ResponseEntity.ok(salle);
    }

    /**
     * Changer le statut d'une salle
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<SalleDTO> changerStatutSalle(
            @PathVariable Long id,
            @RequestParam StatutSalle statut,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de changement de statut de la salle {} vers: {}", id, statut);
        SalleDTO salle = salleService.changerStatutSalle(id, statut, utilisateur);
        return ResponseEntity.ok(salle);
    }

    /**
     * Supprimer une salle
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSalle(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de suppression de la salle: {}", id);
        salleService.supprimerSalle(id, utilisateur);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vérifier si une salle existe
     */
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> salleExiste(@PathVariable Long id) {
        log.debug("Vérification de l'existence de la salle: {}", id);
        boolean existe = salleService.salleExiste(id);
        return ResponseEntity.ok(existe);
    }

    /**
     * Vérifier si un code salle existe
     */
    @GetMapping("/code/{codeSalle}/existe")
    public ResponseEntity<Boolean> codeSalleExiste(@PathVariable String codeSalle) {
        log.debug("Vérification de l'existence du code salle: {}", codeSalle);
        boolean existe = salleService.codeSalleExiste(codeSalle);
        return ResponseEntity.ok(existe);
    }

    /**
     * Obtenir les statistiques des salles
     */
    @GetMapping("/statistiques")
    public ResponseEntity<Object> getStatistiquesSalles() {
        log.debug("Demande de statistiques des salles");
        Object statistiques = salleService.getStatistiquesSalles();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Endpoint de santé pour le service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Salles Service is running");
    }
}