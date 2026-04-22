package iusjc_planning.salles_service.controller;

import iusjc_planning.salles_service.dto.CreateReservationRequest;
import iusjc_planning.salles_service.dto.ReservationDTO;
import iusjc_planning.salles_service.model.StatutReservation;
import iusjc_planning.salles_service.service.ReservationService;
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
 * Contrôleur REST pour la gestion des réservations
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Créer une nouvelle réservation
     */
    @PostMapping
    public ResponseEntity<ReservationDTO> creerReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de création de réservation pour la salle: {} par l'utilisateur: {}",
                request.getSalleId(), request.getUtilisateurId());
        ReservationDTO reservation = reservationService.creerReservation(request, utilisateur);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    /**
     * Récupérer toutes les réservations
     */
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        log.debug("Demande de récupération de toutes les réservations");
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer une réservation par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        log.debug("Demande de récupération de la réservation: {}", id);
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Récupérer les réservations par salle
     */
    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsParSalle(@PathVariable Long salleId) {
        log.debug("Demande de récupération des réservations pour la salle: {}", salleId);
        List<ReservationDTO> reservations = reservationService.getReservationsParSalle(salleId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer les réservations par utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsParUtilisateur(@PathVariable Long utilisateurId) {
        log.debug("Demande de récupération des réservations pour l'utilisateur: {}", utilisateurId);
        List<ReservationDTO> reservations = reservationService.getReservationsParUtilisateur(utilisateurId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer les réservations par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ReservationDTO>> getReservationsParStatut(@PathVariable StatutReservation statut) {
        log.debug("Demande de récupération des réservations avec le statut: {}", statut);
        List<ReservationDTO> reservations = reservationService.getReservationsParStatut(statut);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer les réservations en attente
     */
    @GetMapping("/en-attente")
    public ResponseEntity<List<ReservationDTO>> getReservationsEnAttente() {
        log.debug("Demande de récupération des réservations en attente");
        List<ReservationDTO> reservations = reservationService.getReservationsEnAttente();
        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer les réservations pour une période
     */
    @GetMapping("/periode")
    public ResponseEntity<List<ReservationDTO>> getReservationsPourPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        log.debug("Demande de réservations du {} au {}", dateDebut, dateFin);
        List<ReservationDTO> reservations = reservationService.getReservationsPourPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer les réservations actives
     */
    @GetMapping("/actives")
    public ResponseEntity<List<ReservationDTO>> getReservationsActives() {
        log.debug("Demande de récupération des réservations actives");
        List<ReservationDTO> reservations = reservationService.getReservationsActives();
        return ResponseEntity.ok(reservations);
    }

    /**
     * Valider une réservation
     */
    @PatchMapping("/{id}/valider")
    public ResponseEntity<ReservationDTO> validerReservation(
            @PathVariable Long id,
            @RequestParam Long validateurId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de validation de la réservation: {} par: {}", id, validateurId);
        ReservationDTO reservation = reservationService.validerReservation(id, validateurId, utilisateur);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Rejeter une réservation
     */
    @PatchMapping("/{id}/rejeter")
    public ResponseEntity<ReservationDTO> rejeterReservation(
            @PathVariable Long id,
            @RequestParam String motifRejet,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de rejet de la réservation: {} avec motif: {}", id, motifRejet);
        ReservationDTO reservation = reservationService.rejeterReservation(id, motifRejet, utilisateur);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Annuler une réservation
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<ReservationDTO> annulerReservation(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande d'annulation de la réservation: {}", id);
        ReservationDTO reservation = reservationService.annulerReservation(id, utilisateur);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Mettre à jour une réservation
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> mettreAJourReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDTO reservationDTO,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de mise à jour de la réservation: {}", id);
        ReservationDTO reservation = reservationService.mettreAJourReservation(id, reservationDTO, utilisateur);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Supprimer une réservation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerReservation(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String utilisateur) {

        log.info("Demande de suppression de la réservation: {}", id);
        reservationService.supprimerReservation(id, utilisateur);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenir les statistiques des réservations
     */
    @GetMapping("/statistiques")
    public ResponseEntity<Object> getStatistiquesReservations() {
        log.debug("Demande de statistiques des réservations");
        Object statistiques = reservationService.getStatistiquesReservations();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Endpoint de santé pour le service réservations
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Reservations Service is running");
    }
}