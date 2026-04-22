package iusjc_planning.user_service.mapper;

import iusjc_planning.user_service.dto.CreateEnseignantRequest;
import iusjc_planning.user_service.dto.EnseignantDTO;
import iusjc_planning.user_service.dto.EnseignantResponse;
import iusjc_planning.user_service.model.Enseignant;

import java.util.stream.Collectors;

public class EnseignantMapper {

    public static EnseignantDTO toDto(Enseignant ens) {
        if (ens == null)
            return null;

        EnseignantDTO dto = new EnseignantDTO();
        dto.setId(ens.getId());
        dto.setNom(ens.getNom());
        dto.setPrenom(ens.getPrenom());
        dto.setEmail(ens.getEmail());
        dto.setSpecialite(ens.getSpecialite());
        dto.setGrade(ens.getGrade());
        dto.setStatut(ens.getStatut());
        dto.setMustChangePassword(ens.isMustChangePassword());

        dto.setRole(ens.getRole());

        return dto;
    }

    public static Enseignant toEntity(CreateEnseignantRequest req) {
        Enseignant ens = new Enseignant();
        ens.setNom(req.getNom());
        ens.setPrenom(req.getPrenom());
        ens.setEmail(req.getEmail());
        ens.setPassword(req.getPassword()); // password sera hashé plus tard dans le service
        ens.setSpecialite(req.getSpecialite());
        ens.setGrade(req.getGrade());
        return ens;
    }

    public static EnseignantResponse toResponse(Enseignant ens, String rawPassword, String message) {
        EnseignantResponse res = new EnseignantResponse();

        res.setId(ens.getId());
        res.setNom(ens.getNom());
        res.setPrenom(ens.getPrenom());
        res.setEmail(ens.getEmail());
        res.setSpecialite(ens.getSpecialite());
        res.setGrade(ens.getGrade());
        res.setPassword(rawPassword);
        res.setMessage(message);

        res.setRole(ens.getRole());

        return res;
    }
}
