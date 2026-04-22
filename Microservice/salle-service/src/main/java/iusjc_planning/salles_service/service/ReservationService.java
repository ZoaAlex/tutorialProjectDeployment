package iusjc_planning.salles_service.service;

import iusjc_planning.salles_service.dto.CreateReservationRequest;
import iusjc_planning.salles_service.dto.ReservationDTO;
import iusjc_planning.salles_service.exception.ResourceNotFoundException;
import iusjc_planning.salles_service.exception.BusinessException;
import iusjc_planning.salles_service.mapper.ReservationMapper;
import iusjc_planning.salles_service.model.Reservation;
import iusjc_planning.salles_service.model.Salle;
import iusjc_planning.salles_service.model.StatutReservation;
import iusjc_planning.salles_service.repository.ReservationRepository;
import iusjc_planning.salles_service.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion des réservations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SalleRepository salleRepository;
    private final ReservationMapper reservationMapper;

    /**
     * Créer une nouvelle réservation
     */
    public ReservationDTO creerReservation(CreateReservationRequest request, String utilisateur) {
        log.info("Création d'une nouvelle réservation pour la salle: {} par l'utilisateur: {}",
                request.getSalleId(), request.getUtilisateurId());

        // Vérifier que la salle existe
        Salle salle = salleRepository.findById(request.getSalleId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + request.getSalleId()));

        // Vérifier que la salle est disponible
        if (!salle.isDisponible()) {
            throw new BusinessException("La salle '" + salle.getNom() + "' n'est pas disponible pour réservation");
        }

        // Vérifier les conflits de réservation
        List<Reservation> conflits = reservationRepository.findConflitsReservation(
                request.getSalleId(), request.getDateDebut(), request.getDateFin());

        if (!conflits.isEmpty()) {
            throw new BusinessException("La salle est déjà réservée pour cette période");
        }

        // Vérifier la capacité si spécifiée
        if (request.getNombreParticipants() != null &&
                request.getNombreParticipants() > salle.getCapacite()) {
            throw new BusinessException("Le nombre de participants (" + request.getNombreParticipants() +
                    ") dépasse la capacité de la salle (" + salle.getCapacite() + ")");
        }

        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setSalle(salle);
        reservation.setCreePar(utilisateur);
        reservation.setDateCreation(LocalDateTime.now());

        Reservation reservationSauvegardee = reservationRepository.save(reservation);
        log.info("Réservation créée avec succès - ID: {}", reservationSauvegardee.getId());

        return reservationMapper.toDTO(reservationSauvegardee);
    }

    /**
     * Récupérer une réservation par son ID
     */
    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Long id) {
        log.debug("Récupération de la réservation avec l'ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        return reservationMapper.toDTO(reservation);
    }

    /**
     * Récupérer toutes les réservations
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getAllReservations() {
        log.debug("Récupération de toutes les réservations");

        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Récupérer les réservations par salle
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsParSalle(Long salleId) {
        log.debug("Récupération des réservations pour la salle: {}", salleId);

        List<Reservation> reservations = reservationRepository.findBySalleId(salleId);
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Récupérer les réservations par utilisateur
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsParUtilisateur(Long utilisateurId) {
        log.debug("Récupération des réservations pour l'utilisateur: {}", utilisateurId);

        List<Reservation> reservations = reservationRepository.findByUtilisateurId(utilisateurId);
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Récupérer les réservations par statut
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsParStatut(StatutReservation statut) {
        log.debug("Récupération des réservations avec le statut: {}", statut);

        List<Reservation> reservations = reservationRepository.findByStatut(statut);
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Récupérer les réservations en attente
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsEnAttente() {
        log.debug("Récupération des réservations en attente");

        List<Reservation> reservations = reservationRepository.findReservationsEnAttente();
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Récupérer les réservations pour une période
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsPourPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.debug("Récupération des réservations du {} au {}", dateDebut, dateFin);

        List<Reservation> reservations = reservationRepository.findReservationsValideesPourPeriode(dateDebut, dateFin);
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Récupérer les réservations actives
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsActives() {
        log.debug("Récupération des réservations actives");

        List<Reservation> reservations = reservationRepository.findReservationsActives(LocalDateTime.now());
        return reservationMapper.toDTOList(reservations);
    }

    /**
     * Valider une réservation
     */
    public ReservationDTO validerReservation(Long id, Long validateurId, String utilisateur) {
        log.info("Validation de la réservation: {} par: {}", id, validateurId);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BusinessException("Seules les réservations en attente peuvent être validées");
        }

        // Vérifier à nouveau les conflits au moment de la validation
        List<Reservation> conflits = reservationRepository.findConflitsReservation(
                reservation.getSalle().getId(), reservation.getDateDebut(), reservation.getDateFin());

        if (!conflits.isEmpty()) {
            throw new BusinessException("Un conflit de réservation a été détecté. Validation impossible.");
        }

        reservation.setStatut(StatutReservation.VALIDEE);
        reservation.setValideePar(validateurId);
        reservation.setDateValidation(LocalDateTime.now());
        reservation.setModifiePar(utilisateur);
        reservation.setDateModification(LocalDateTime.now());

        Reservation reservationValidee = reservationRepository.save(reservation);
        log.info("Réservation validée avec succès");

        return reservationMapper.toDTO(reservationValidee);
    }

    /**
     * Rejeter une réservation
     */
    public ReservationDTO rejeterReservation(Long id, String motifRejet, String utilisateur) {
        log.info("Rejet de la réservation: {} avec motif: {}", id, motifRejet);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BusinessException("Seules les réservations en attente peuvent être rejetées");
        }

        reservation.setStatut(StatutReservation.REJETEE);
        reservation.setMotifRejet(motifRejet);
        reservation.setModifiePar(utilisateur);
        reservation.setDateModification(LocalDateTime.now());

        Reservation reservationRejetee = reservationRepository.save(reservation);
        log.info("Réservation rejetée avec succès");

        return reservationMapper.toDTO(reservationRejetee);
    }

    /**
     * Annuler une réservation
     */
    public ReservationDTO annulerReservation(Long id, String utilisateur) {
        log.info("Annulation de la réservation: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (reservation.getStatut() == StatutReservation.TERMINEE) {
            throw new BusinessException("Une réservation terminée ne peut pas être annulée");
        }

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessException("Cette réservation est déjà annulée");
        }

        reservation.setStatut(StatutReservation.ANNULEE);
        reservation.setModifiePar(utilisateur);
        reservation.setDateModification(LocalDateTime.now());

        Reservation reservationAnnulee = reservationRepository.save(reservation);
        log.info("Réservation annulée avec succès");

        return reservationMapper.toDTO(reservationAnnulee);
    }

    /**
     * Mettre à jour une réservation
     */
    public ReservationDTO mettreAJourReservation(Long id, ReservationDTO reservationDTO, String utilisateur) {
        log.info("Mise à jour de la réservation avec l'ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (!reservation.isActive() && reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BusinessException("Cette réservation ne peut plus être modifiée");
        }

        // Vérifier les conflits si les dates changent
        if (!reservation.getDateDebut().equals(reservationDTO.getDateDebut()) ||
                !reservation.getDateFin().equals(reservationDTO.getDateFin())) {

            List<Reservation> conflits = reservationRepository.findConflitsReservation(
                    reservation.getSalle().getId(), reservationDTO.getDateDebut(), reservationDTO.getDateFin());

            // Exclure la réservation actuelle des conflits
            conflits = conflits.stream().filter(r -> !r.getId().equals(id)).toList();

            if (!conflits.isEmpty()) {
                throw new BusinessException("La salle est déjà réservée pour cette nouvelle période");
            }
        }

        reservationMapper.updateEntity(reservation, reservationDTO);
        reservation.setModifiePar(utilisateur);

        Reservation reservationModifiee = reservationRepository.save(reservation);
        log.info("Réservation mise à jour avec succès");

        return reservationMapper.toDTO(reservationModifiee);
    }

    /**
     * Supprimer une réservation
     */
    public void supprimerReservation(Long id, String utilisateur) {
        log.info("Suppression de la réservation avec l'ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (reservation.isActive()) {
            throw new BusinessException("Impossible de supprimer une réservation active");
        }

        reservationRepository.delete(reservation);
        log.info("Réservation supprimée avec succès");
    }

    /**
     * Obtenir les statistiques des réservations
     */
    @Transactional(readOnly = true)
    public Object getStatistiquesReservations() {
        log.debug("Récupération des statistiques des réservations");

        return new Object() {
            public final List<Object[]> parStatut = reservationRepository.getStatistiquesParStatut();
            public final List<Object[]> parMois = reservationRepository.getStatistiquesParMois();
            public final long totalReservations = reservationRepository.count();
            public final long reservationsEnAttente = reservationRepository.countByStatut(StatutReservation.EN_ATTENTE);
            public final long reservationsValidees = reservationRepository.countByStatut(StatutReservation.VALIDEE);
            public final long reservationsActives = reservationRepository.findReservationsActives(LocalDateTime.now())
                    .size();
        };
    }
}