package iusjc_planning.planning_service.dto;

import iusjc_planning.planning_service.model.JourSemaine;
import iusjc_planning.planning_service.model.TypeDisponibilite;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDisponibiliteRequest {

    @NotNull(message = "Le jour est obligatoire")
    private JourSemaine jour;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotNull(message = "Le type de disponibilité est obligatoire")
    private TypeDisponibilite type;

    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;

    @Builder.Default
    private Boolean estDisponible = true;

    @NotNull(message = "L'ID de l'enseignant est obligatoire")
    private Long enseignantId;
}