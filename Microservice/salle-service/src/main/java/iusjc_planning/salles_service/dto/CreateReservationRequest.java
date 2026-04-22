package iusjc_planning.salles_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la création d'une réservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    
    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;
    
    @NotBlank(message = "Le motif de réservation est obligatoire")
    @Size(max = 200, message = "Le motif ne peut pas dépasser 200 caractères")
    private String motif;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @NotNull(message = "L'utilisateur réservant est obligatoire")
    private Long utilisateurId;
    
    @NotNull(message = "La salle est obligatoire")
    private Long salleId;
    
    @Min(value = 1, message = "Le nombre de participants doit être au moins de 1")
    private Integer nombreParticipants;
    
    private String materielRequis;
    private String commentaires;
    
    @Min(value = 1, message = "La priorité doit être entre 1 et 3")
    @Max(value = 3, message = "La priorité doit être entre 1 et 3")
    private Integer priorite = 1;
    
    private Boolean recurrente = false;
    private String frequenceRecurrence;
    private LocalDateTime dateFinRecurrence;
    
    // Validation personnalisée
    @AssertTrue(message = "La date de fin doit être après la date de début")
    public boolean isDateFinApresDateDebut() {
        if (dateDebut == null || dateFin == null) {
            return true; // Laisse les autres validations gérer les nulls
        }
        return dateFin.isAfter(dateDebut);
    }
    
    @AssertTrue(message = "La durée de réservation ne peut pas dépasser 12 heures")
    public boolean isDureeRaisonnable() {
        if (dateDebut == null || dateFin == null) {
            return true;
        }
        long heures = java.time.Duration.between(dateDebut, dateFin).toHours();
        return heures <= 12;
    }
    
    @AssertTrue(message = "Pour une réservation récurrente, la fréquence et la date de fin sont obligatoires")
    public boolean isRecurrenceValide() {
        if (recurrente == null || !recurrente) {
            return true;
        }
        return frequenceRecurrence != null && !frequenceRecurrence.trim().isEmpty() 
               && dateFinRecurrence != null;
    }
}