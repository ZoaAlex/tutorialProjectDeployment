package org.example.coursclasseservice.mapper;

import org.example.coursclasseservice.dto.EtudiantDto;
import org.example.coursclasseservice.model.Etudiant;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.GroupeEtudiant;
import java.util.List;
import java.util.stream.Collectors;

public class EtudiantMapper {
    public static EtudiantDto toDto(Etudiant entity) {
        if (entity == null) return null;
        EtudiantDto dto = new EtudiantDto();
        dto.setId(entity.getId());
        dto.setMatricule(entity.getMatricule());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setSex(entity.getSex());
        dto.setClasseId(entity.getClasse() != null ? entity.getClasse().getId() : null);
        if (entity.getGroupes() != null) {
            dto.setGroupeIds(entity.getGroupes().stream().map(GroupeEtudiant::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Etudiant toEntity(EtudiantDto dto, Classe classe, List<GroupeEtudiant> groupes) {
        if (dto == null) return null;
        Etudiant entity = new Etudiant();
        entity.setId(dto.getId());
        entity.setMatricule(dto.getMatricule());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setSex(dto.getSex());
        entity.setClasse(classe);
        entity.setGroupes(groupes);
        return entity;
    }
}
