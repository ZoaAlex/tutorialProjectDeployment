package iusjc_planning.planning_service.mapper;

import iusjc_planning.planning_service.dto.CreateDisponibiliteRequest;
import iusjc_planning.planning_service.dto.DisponibiliteEnseignantDTO;
import iusjc_planning.planning_service.model.DisponibiliteEnseignant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DisponibiliteEnseignantMapper {

    public DisponibiliteEnseignantDTO toDTO(DisponibiliteEnseignant disponibilite) {
        if (disponibilite == null) {
            return null;
        }

        return DisponibiliteEnseignantDTO.builder()
                .id(disponibilite.getId())
                .jour(disponibilite.getJour())
                .heureDebut(disponibilite.getHeureDebut())
                .heureFin(disponibilite.getHeureFin())
                .type(disponibilite.getType())
                .commentaire(disponibilite.getCommentaire())
                .estDisponible(disponibilite.getEstDisponible())
                .enseignantId(disponibilite.getEnseignantId())
                .build();
    }

    public List<DisponibiliteEnseignantDTO> toDTOList(List<DisponibiliteEnseignant> disponibilites) {
        return disponibilites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DisponibiliteEnseignant toEntity(CreateDisponibiliteRequest request, Long enseignantId) {
        if (request == null) {
            return null;
        }

        return DisponibiliteEnseignant.builder()
                .jour(request.getJour())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .type(request.getType())
                .commentaire(request.getCommentaire())
                .estDisponible(request.getEstDisponible())
                .enseignantId(enseignantId)
                .build();
    }
}