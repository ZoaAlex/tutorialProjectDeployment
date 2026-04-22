package iusjc_planning.salles_service.dto;

import iusjc_planning.salles_service.model.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour l'entité Réservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String motif;
    private String description;
    private StatutReservation statut;
    private Long utilisateurId;
    private String nomUtilisateur;
    private Integer nombreParticipants;
    private String materielRequis;
    private String commentaires;
    private Long valideePar;
    private String nomValidateur;
    private LocalDateTime dateValidation;
    private String motifRejet;
    private Integer priorite;
    private Boolean recurrente;
    private String frequenceRecurrence;
    private LocalDateTime dateFinRecurrence;
    private Long salleId;
    private String nomSalle;
    private String codeSalle;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;
    
    // Informations calculées
    private Long dureeEnMinutes;
    private Boolean estActive;
    private Boolean estTerminee;
    private Boolean peutEtreModifiee;
}