package iusjc_planning.salles_service.dto;

import iusjc_planning.salles_service.model.TypeSalle;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'une salle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalleRequest {

    @NotBlank(message = "Le code de la salle est obligatoire")
    @Size(max = 20, message = "Le code ne peut pas dépasser 20 caractères")
    private String codeSalle;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins de 1")
    @Max(value = 1000, message = "La capacité ne peut pas dépasser 1000")
    private Integer capacite;

    @NotNull(message = "Le type de salle est obligatoire")
    private TypeSalle typeSalle;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @Size(max = 200, message = "L'emplacement ne peut pas dépasser 200 caractères")
    private String emplacement;

    private Integer etage;
    private String batiment;
    private Double surface;
    private Boolean accessibleHandicap = false;
    private Boolean climatisee = false;
    private Boolean wifiDisponible = true;
}