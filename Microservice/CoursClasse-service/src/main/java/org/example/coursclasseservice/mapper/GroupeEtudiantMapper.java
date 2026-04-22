package org.example.coursclasseservice.mapper;

import org.example.coursclasseservice.dto.GroupeEtudiantDto;
import org.example.coursclasseservice.model.GroupeEtudiant;
import org.example.coursclasseservice.model.Etudiant;
import org.example.coursclasseservice.model.Classe;
import java.util.List;
import java.util.stream.Collectors;

public class GroupeEtudiantMapper {
    public static GroupeEtudiantDto toDto(GroupeEtudiant entity) {
        if (entity == null) return null;
        GroupeEtudiantDto dto = new GroupeEtudiantDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setEffectif(entity.getEffectif());
        if (entity.getEtudiants() != null) {
            dto.setEtudiantIds(entity.getEtudiants().stream().map(Etudiant::getId).collect(Collectors.toList()));
        }
        if (entity.getClasses() != null) {
            dto.setClasseIds(entity.getClasses().stream().map(Classe::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static GroupeEtudiant toEntity(GroupeEtudiantDto dto, List<Etudiant> etudiants, List<Classe> classes) {
        if (dto == null) return null;
        GroupeEtudiant entity = new GroupeEtudiant();
        entity.setId(dto.getId());
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setEffectif(dto.getEffectif());
        entity.setEtudiants(etudiants);
        entity.setClasses(classes);
        return entity;
    }
}
