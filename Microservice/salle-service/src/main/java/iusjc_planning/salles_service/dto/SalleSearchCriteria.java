package iusjc_planning.salles_service.dto;

import iusjc_planning.salles_service.model.StatutSalle;
import iusjc_planning.salles_service.model.TypeSalle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les critères de recherche de salles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalleSearchCriteria {
    private TypeSalle typeSalle;
    private Integer capaciteMin;
    private Integer capaciteMax;
    private StatutSalle statut;
    private Boolean accessibleHandicap;
    private Boolean climatisee;
    private Boolean wifiDisponible;
    private String batiment;
    private Integer etage;
    private String searchTerm;

    // Critères de disponibilité
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Boolean disponiblePourPeriode;

    // Critères de matériel
    private String typeMaterielRequis;
    private Boolean avecMaterielFonctionnel;
}