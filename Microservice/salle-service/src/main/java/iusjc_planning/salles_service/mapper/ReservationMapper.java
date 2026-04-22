package iusjc_planning.salles_service.mapper;

import iusjc_planning.salles_service.dto.CreateReservationRequest;
import iusjc_planning.salles_service.dto.ReservationDTO;
import iusjc_planning.salles_service.model.Reservation;
import iusjc_planning.salles_service.model.StatutReservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre Reservation et ReservationDTO
 */
@Component
public class ReservationMapper {

    /**
     * Convertit une entité Reservation en ReservationDTO
     */
    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setMotif(reservation.getMotif());
        dto.setDescription(reservation.getDescription());
        dto.setStatut(reservation.getStatut());
        dto.setUtilisateurId(reservation.getUtilisateurId());
        dto.setNombreParticipants(reservation.getNombreParticipants());
        dto.setMaterielRequis(reservation.getMaterielRequis());
        dto.setCommentaires(reservation.getCommentaires());
        dto.setValideePar(reservation.getValideePar());
        dto.setDateValidation(reservation.getDateValidation());
        dto.setMotifRejet(reservation.getMotifRejet());
        dto.setPriorite(reservation.getPriorite());
        dto.setRecurrente(reservation.getRecurrente());
        dto.setFrequenceRecurrence(reservation.getFrequenceRecurrence());
        dto.setDateFinRecurrence(reservation.getDateFinRecurrence());
        dto.setDateCreation(reservation.getDateCreation());
        dto.setDateModification(reservation.getDateModification());
        dto.setCreePar(reservation.getCreePar());
        dto.setModifiePar(reservation.getModifiePar());

        // Informations de la salle
        if (reservation.getSalle() != null) {
            dto.setSalleId(reservation.getSalle().getId());
            dto.setNomSalle(reservation.getSalle().getNom());
            dto.setCodeSalle(reservation.getSalle().getCodeSalle());
        }

        // Informations calculées
        dto.setDureeEnMinutes(reservation.getDureeEnMinutes());
        dto.setEstActive(reservation.isActive());
        dto.setEstTerminee(reservation.isTerminee());
        dto.setPeutEtreModifiee(peutEtreModifiee(reservation));

        return dto;
    }

    /**
     * Convertit une liste d'entités Reservation en liste de ReservationDTO
     */
    public List<ReservationDTO> toDTOList(List<Reservation> reservations) {
        if (reservations == null) {
            return null;
        }
        return reservations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un CreateReservationRequest en entité Reservation
     */
    public Reservation toEntity(CreateReservationRequest request) {
        if (request == null) {
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setMotif(request.getMotif());
        reservation.setDescription(request.getDescription());
        reservation.setStatut(StatutReservation.EN_ATTENTE); // Statut par défaut
        reservation.setUtilisateurId(request.getUtilisateurId());
        reservation.setNombreParticipants(request.getNombreParticipants());
        reservation.setMaterielRequis(request.getMaterielRequis());
        reservation.setCommentaires(request.getCommentaires());
        reservation.setPriorite(request.getPriorite());
        reservation.setRecurrente(request.getRecurrente());
        reservation.setFrequenceRecurrence(request.getFrequenceRecurrence());
        reservation.setDateFinRecurrence(request.getDateFinRecurrence());

        return reservation;
    }

    /**
     * Met à jour une entité Reservation existante avec les données d'un ReservationDTO
     */
    public void updateEntity(Reservation reservation, ReservationDTO dto) {
        if (reservation == null || dto == null) {
            return;
        }

        reservation.setDateDebut(dto.getDateDebut());
        reservation.setDateFin(dto.getDateFin());
        reservation.setMotif(dto.getMotif());
        reservation.setDescription(dto.getDescription());
        reservation.setNombreParticipants(dto.getNombreParticipants());
        reservation.setMaterielRequis(dto.getMaterielRequis());
        reservation.setCommentaires(dto.getCommentaires());
        reservation.setPriorite(dto.getPriorite());
        reservation.setRecurrente(dto.getRecurrente());
        reservation.setFrequenceRecurrence(dto.getFrequenceRecurrence());
        reservation.setDateFinRecurrence(dto.getDateFinRecurrence());
        reservation.setModifiePar(dto.getModifiePar());
        reservation.setDateModification(LocalDateTime.now());
    }

    /**
     * Détermine si une réservation peut être modifiée
     */
    private boolean peutEtreModifiee(Reservation reservation) {
        if (reservation == null) {
            return false;
        }

        // Une réservation peut être modifiée si :
        // - Elle est en attente
        // - Elle est validée mais n'a pas encore commencé
        // - Elle n'est pas terminée ou annulée
        LocalDateTime maintenant = LocalDateTime.now();
        
        return (reservation.getStatut() == StatutReservation.EN_ATTENTE) ||
               (reservation.getStatut() == StatutReservation.VALIDEE && 
                reservation.getDateDebut().isAfter(maintenant));
    }
}