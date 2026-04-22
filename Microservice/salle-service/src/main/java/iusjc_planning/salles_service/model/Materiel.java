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
 * Entité représentant le matériel d'une salle
 * Basée sur le diagramme de classe
 */
@Entity
@Table(name = "materiels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du matériel est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeMateriel type;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "quantite_fonctionnelle")
    private Integer quantiteFonctionnelle;

    @Column(name = "marque")
    private String marque;

    @Column(name = "modele")
    private String modele;

    @Column(name = "numero_serie")
    private String numeroSerie;

    @Column(name = "date_acquisition")
    private LocalDateTime dateAcquisition;

    @Column(name = "date_derniere_maintenance")
    private LocalDateTime dateDerniereMaintenance;

    @Column(name = "date_prochaine_maintenance")
    private LocalDateTime dateProchaineMaintenance;

    @Column(name = "etat")
    private String etat = "FONCTIONNEL"; // FONCTIONNEL, EN_PANNE, EN_MAINTENANCE

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

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
    public boolean isFonctionnel() {
        return "FONCTIONNEL".equals(this.etat);
    }

    public boolean isEnPanne() {
        return "EN_PANNE".equals(this.etat);
    }

    public boolean isEnMaintenance() {
        return "EN_MAINTENANCE".equals(this.etat);
    }

    public boolean isMaintenanceRequise() {
        return dateProchaineMaintenance != null && 
               dateProchaineMaintenance.isBefore(LocalDateTime.now());
    }
}