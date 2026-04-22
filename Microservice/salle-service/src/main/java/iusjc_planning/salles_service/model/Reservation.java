package iusjc_planning.salles_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une réservation de salle
 * Basée sur le diagramme de classe
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @NotBlank(message = "Le motif de réservation est obligatoire")
    @Size(max = 200, message = "Le motif ne peut pas dépasser 200 caractères")
    @Column(name = "motif", nullable = false)
    private String motif;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @NotNull(message = "L'utilisateur réservant est obligatoire")
    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "nombre_participants")
    private Integer nombreParticipants;

    @Column(name = "materiel_requis", columnDefinition = "TEXT")
    private String materielRequis;

    @Column(name = "commentaires", columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "validee_par")
    private Long valideePar;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @Column(name = "priorite")
    private Integer priorite = 1; // 1=Normale, 2=Haute, 3=Urgente

    @Column(name = "recurrente")
    private Boolean recurrente = false;

    @Column(name = "frequence_recurrence")
    private String frequenceRecurrence; // HEBDOMADAIRE, MENSUELLE, etc.

    @Column(name = "date_fin_recurrence")
    private LocalDateTime dateFinRecurrence;

    // Relation avec Salle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    // Timestamps
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "cree_par")
    private String creePar;

    @Column(name = "modifie_par")
    private String modifiePar;

    // Méthodes utilitaires
    public boolean isEnAttente() {
        return this.statut == StatutReservation.EN_ATTENTE;
    }

    public boolean isValidee() {
        return this.statut == StatutReservation.VALIDEE;
    }

    public boolean isRejetee() {
        return this.statut == StatutReservation.REJETEE;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return this.statut == StatutReservation.VALIDEE && 
               now.isAfter(this.dateDebut) && 
               now.isBefore(this.dateFin);
    }

    public boolean isTerminee() {
        return this.statut == StatutReservation.TERMINEE || 
               LocalDateTime.now().isAfter(this.dateFin);
    }

    public long getDureeEnMinutes() {
        return java.time.Duration.between(dateDebut, dateFin).toMinutes();
    }
}