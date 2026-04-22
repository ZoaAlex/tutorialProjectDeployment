package org.example.coursclasseservice.mapper;

import org.example.coursclasseservice.dto.EcoleDto;
import org.example.coursclasseservice.model.Ecole;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Filiere;
import java.util.List;
import java.util.stream.Collectors;

public class EcoleMapper {
    public static EcoleDto toDto(Ecole entity) {
        if (entity == null) return null;
        EcoleDto dto = new EcoleDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        if (entity.getFilieres() != null) {
            dto.setFiliereIds(entity.getFilieres().stream().map(Filiere::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Ecole toEntity(EcoleDto dto, List<Classe> classes, List<Filiere> filieres) {
        if (dto == null) return null;
        Ecole entity = new Ecole();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setFilieres(filieres);
        return entity;
    }
}
