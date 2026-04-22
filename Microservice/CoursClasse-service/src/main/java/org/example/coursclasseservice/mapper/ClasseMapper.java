package org.example.coursclasseservice.mapper;

import org.example.coursclasseservice.dto.ClasseDto;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Etudiant;
import java.util.List;
import java.util.stream.Collectors;
import org.example.coursclasseservice.model.Filiere;

public class ClasseMapper {
    public static ClasseDto toDto(Classe entity) {
        if (entity == null)
            return null;
        ClasseDto dto = new ClasseDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNom(entity.getNom());
        dto.setEffectif(entity.getEffectif());
        dto.setFiliereId(entity.getFiliere() != null ? entity.getFiliere().getId() : null);
        dto.setSalleId(entity.getSalleId());
        if (entity.getEtudiants() != null) {
            dto.setEtudiantIds(entity.getEtudiants().stream().map(Etudiant::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Classe toEntity(ClasseDto dto, List<Etudiant> etudiants, Filiere filiere) {
        if (dto == null)
            return null;
        Classe entity = new Classe();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNom(dto.getNom());
        entity.setEffectif(dto.getEffectif());
        entity.setSalleId(dto.getSalleId());
        entity.setEtudiants(etudiants);
        entity.setFiliere(filiere);
        return entity;
    }
}
