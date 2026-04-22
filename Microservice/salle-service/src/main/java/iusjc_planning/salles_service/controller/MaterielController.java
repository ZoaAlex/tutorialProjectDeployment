package iusjc_planning.salles_service.controller;

import iusjc_planning.salles_service.dto.CreateMaterielRequest;
import iusjc_planning.salles_service.dto.MaterielDTO;
import iusjc_planning.salles_service.model.TypeMateriel;
import iusjc_planning.salles_service.service.MaterielService;
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
 * Contrôleur REST pour la gestion du matériel
 */
@RestController
@RequestMapping("/api/materiels")
@RequiredArgsConstructor
@Slf4j
public class MaterielController {

    private final MaterielService materielService;

    /**
     * Créer un nouveau matériel
     */
    @PostMapping
    public ResponseEntity<MaterielDTO> creerMateriel(
            @Valid @RequestBody CreateMaterielRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de création de matériel: {} pour la salle: {}", request.getNom(), request.getSalleId());
        MaterielDTO materiel = materielService.creerMateriel(request, utilisateur);
        return new ResponseEntity<>(materiel, HttpStatus.CREATED);
    }

    /**
     * Récupérer tout le matériel
     */
    @GetMapping
    public ResponseEntity<List<MaterielDTO>> getAllMateriel() {
        log.debug("Demande de récupération de tout le matériel");
        List<MaterielDTO> materiels = materielService.getAllMateriel();
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer un matériel par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaterielDTO> getMaterielById(@PathVariable Long id) {
        log.debug("Demande de récupération du matériel: {}", id);
        MaterielDTO materiel = materielService.getMaterielById(id);
        return ResponseEntity.ok(materiel);
    }

    /**
     * Récupérer le matériel d'une salle
     */
    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<MaterielDTO>> getMaterielParSalle(@PathVariable Long salleId) {
        log.debug("Demande de récupération du matériel pour la salle: {}", salleId);
        List<MaterielDTO> materiels = materielService.getMaterielParSalle(salleId);
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer le matériel fonctionnel d'une salle
     */
    @GetMapping("/salle/{salleId}/fonctionnel")
    public ResponseEntity<List<MaterielDTO>> getMaterielFonctionnelParSalle(@PathVariable Long salleId) {
        log.debug("Demande de récupération du matériel fonctionnel pour la salle: {}", salleId);
        List<MaterielDTO> materiels = materielService.getMaterielFonctionnelParSalle(salleId);
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer le matériel par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MaterielDTO>> getMaterielParType(@PathVariable TypeMateriel type) {
        log.debug("Demande de récupération du matériel de type: {}", type);
        List<MaterielDTO> materiels = materielService.getMaterielParType(type);
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer le matériel par état
     */
    @GetMapping("/etat/{etat}")
    public ResponseEntity<List<MaterielDTO>> getMaterielParEtat(@PathVariable String etat) {
        log.debug("Demande de récupération du matériel avec l'état: {}", etat);
        List<MaterielDTO> materiels = materielService.getMaterielParEtat(etat);
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer le matériel nécessitant une maintenance
     */
    @GetMapping("/maintenance/requise")
    public ResponseEntity<List<MaterielDTO>> getMaterielNecessitantMaintenance() {
        log.debug("Demande de récupération du matériel nécessitant une maintenance");
        List<MaterielDTO> materiels = materielService.getMaterielNecessitantMaintenance();
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer le matériel en panne
     */
    @GetMapping("/panne")
    public ResponseEntity<List<MaterielDTO>> getMaterielEnPanne() {
        log.debug("Demande de récupération du matériel en panne");
        List<MaterielDTO> materiels = materielService.getMaterielEnPanne();
        return ResponseEntity.ok(materiels);
    }

    /**
     * Récupérer le matériel en maintenance
     */
    @GetMapping("/maintenance")
    public ResponseEntity<List<MaterielDTO>> getMaterielEnMaintenance() {
        log.debug("Demande de récupération du matériel en maintenance");
        List<MaterielDTO> materiels = materielService.getMaterielEnMaintenance();
        return ResponseEntity.ok(materiels);
    }

    /**
     * Rechercher du matériel par terme
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<MaterielDTO>> rechercherMateriel(@RequestParam String terme) {
        log.debug("Demande de recherche de matériel avec le terme: {}", terme);
        List<MaterielDTO> materiels = materielService.rechercherMateriel(terme);
        return ResponseEntity.ok(materiels);
    }

    /**
     * Mettre à jour un matériel
     */
    @PutMapping("/{id}")
    public ResponseEntity<MaterielDTO> mettreAJourMateriel(
            @PathVariable Long id,
            @Valid @RequestBody MaterielDTO materielDTO,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de mise à jour du matériel: {}", id);
        MaterielDTO materiel = materielService.mettreAJourMateriel(id, materielDTO, utilisateur);
        return ResponseEntity.ok(materiel);
    }

    /**
     * Changer l'état d'un matériel
     */
    @PatchMapping("/{id}/etat")
    public ResponseEntity<MaterielDTO> changerEtatMateriel(
            @PathVariable Long id,
            @RequestParam String etat,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de changement d'état du matériel {} vers: {}", id, etat);
        MaterielDTO materiel = materielService.changerEtatMateriel(id, etat, utilisateur);
        return ResponseEntity.ok(materiel);
    }

    /**
     * Programmer une maintenance
     */
    @PatchMapping("/{id}/maintenance")
    public ResponseEntity<MaterielDTO> programmerMaintenance(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMaintenance,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de programmation de maintenance pour le matériel {} le: {}", id, dateMaintenance);
        MaterielDTO materiel = materielService.programmerMaintenance(id, dateMaintenance, utilisateur);
        return ResponseEntity.ok(materiel);
    }

    /**
     * Déplacer un matériel vers une autre salle
     */
    @PatchMapping("/{materielId}/deplacer/{nouvelleSalleId}")
    public ResponseEntity<MaterielDTO> deplacerMateriel(
            @PathVariable Long materielId,
            @PathVariable Long nouvelleSalleId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de déplacement du matériel {} vers la salle: {}", materielId, nouvelleSalleId);
        MaterielDTO materiel = materielService.deplacerMateriel(materielId, nouvelleSalleId, utilisateur);
        return ResponseEntity.ok(materiel);
    }

    /**
     * Supprimer un matériel
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerMateriel(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de suppression du matériel: {}", id);
        materielService.supprimerMateriel(id, utilisateur);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenir les statistiques du matériel
     */
    @GetMapping("/statistiques")
    public ResponseEntity<Object> getStatistiquesMateriel() {
        log.debug("Demande de statistiques du matériel");
        Object statistiques = materielService.getStatistiquesMateriel();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Endpoint de santé pour le service matériel
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Materiel Service is running");
    }
}