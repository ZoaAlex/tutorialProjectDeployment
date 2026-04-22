package iusjc_planning.planning_service.dto;

import iusjc_planning.planning_service.model.JourSemaine;
import iusjc_planning.planning_service.model.TypeDisponibilite;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteEnseignantDTO {
    private Long id;
    private JourSemaine jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeDisponibilite type;
    private String commentaire;
    private Boolean estDisponible;
    private Long enseignantId;
}