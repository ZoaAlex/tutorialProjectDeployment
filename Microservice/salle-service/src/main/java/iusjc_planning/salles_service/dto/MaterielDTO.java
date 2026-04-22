package iusjc_planning.salles_service.dto;

import iusjc_planning.salles_service.model.TypeMateriel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour l'entité Matériel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterielDTO {
    private Long id;
    private String nom;
    private TypeMateriel type;
    private String description;
    private Integer quantite;
    private Integer quantiteFonctionnelle;
    private String marque;
    private String modele;
    private String numeroSerie;
    private LocalDateTime dateAcquisition;
    private LocalDateTime dateDerniereMaintenance;
    private LocalDateTime dateProchaineMaintenance;
    private String etat;
    private String observations;
    private Long salleId;
    private String nomSalle;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;
    
    // Informations calculées
    private Boolean maintenanceRequise;
    private Integer joursDepuisDerniereMaintenance;
    private Integer joursAvantProchaineMaintenance;
}