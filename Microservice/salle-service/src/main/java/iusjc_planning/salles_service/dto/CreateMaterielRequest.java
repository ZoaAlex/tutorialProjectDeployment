package iusjc_planning.salles_service.dto;

import iusjc_planning.salles_service.model.TypeMateriel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la création de matériel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMaterielRequest {
    
    @NotBlank(message = "Le nom du matériel est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @NotNull(message = "Le type de matériel est obligatoire")
    private TypeMateriel type;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantite;
    
    private Integer quantiteFonctionnelle;
    private String marque;
    private String modele;
    private String numeroSerie;
    private LocalDateTime dateAcquisition;
    private LocalDateTime dateProchaineMaintenance;
    private String observations;
    
    @NotNull(message = "La salle est obligatoire")
    private Long salleId;
}