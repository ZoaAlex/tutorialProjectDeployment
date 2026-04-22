package iusjc_planning.salles_service.dto;

import iusjc_planning.salles_service.model.StatutSalle;
import iusjc_planning.salles_service.model.TypeSalle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour l'entité Salle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalleDTO {
    private Long id;
    private String codeSalle;
    private String nom;
    private Integer capacite;
    private TypeSalle typeSalle;
    private StatutSalle statut;
    private String description;
    private String emplacement;
    private Integer etage;
    private String batiment;
    private Double surface;
    private Boolean accessibleHandicap;
    private Boolean climatisee;
    private Boolean wifiDisponible;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    // Informations supplémentaires pour les réponses
    private List<MaterielDTO> materiels;
    private Long nombreReservationsActives;
    private Boolean disponibleMaintenant;
}