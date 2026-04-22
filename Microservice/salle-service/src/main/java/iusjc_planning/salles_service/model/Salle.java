package iusjc_planning.salles_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant une salle
 * Basée sur le diagramme de classe
 */
@Entity
@Table(name = "salles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code de la salle est obligatoire")
    @Size(max = 20, message = "Le code ne peut pas dépasser 20 caractères")
    @Column(name = "code_salle", unique = true, nullable = false)
    private String codeSalle;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins de 1")
    @Max(value = 1000, message = "La capacité ne peut pas dépasser 1000")
    @Column(name = "capacite", nullable = false)
    private Integer capacite;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_salle", nullable = false)
    private TypeSalle typeSalle;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutSalle statut = StatutSalle.LIBRE;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 200, message = "L'emplacement ne peut pas dépasser 200 caractères")
    @Column(name = "emplacement")
    private String emplacement;

    @Column(name = "etage")
    private Integer etage;

    @Column(name = "batiment")
    private String batiment;

    @Column(name = "surface")
    private Double surface;

    @Column(name = "accessible_handicap")
    private Boolean accessibleHandicap = false;

    @Column(name = "climatisee")
    private Boolean climatisee = false;

    @Column(name = "wifi_disponible")
    private Boolean wifiDisponible = true;

    // Relations
    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Materiel> materiels;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

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
    public boolean isDisponible() {
        return this.statut == StatutSalle.LIBRE;
    }

    public boolean isOccupee() {
        return this.statut == StatutSalle.OCCUPEE;
    }

    public boolean isEnMaintenance() {
        return this.statut == StatutSalle.MAINTENANCE;
    }
}