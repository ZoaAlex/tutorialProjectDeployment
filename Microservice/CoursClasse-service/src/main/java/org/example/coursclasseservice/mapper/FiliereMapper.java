package org.example.coursclasseservice.mapper;

import org.example.coursclasseservice.dto.FiliereDto;
import org.example.coursclasseservice.model.Filiere;
import org.example.coursclasseservice.model.Ecole;
import org.example.coursclasseservice.model.Classe;

import java.util.List;
import java.util.stream.Collectors;

public class FiliereMapper {

    public static FiliereDto toDto(Filiere entity) {
        if (entity == null) return null;
        FiliereDto dto = new FiliereDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNom(entity.getNom());
        dto.setEcoleId(entity.getEcole() != null ? entity.getEcole().getId() : null);
        if (entity.getClasses() != null) {
            dto.setClasseIds(entity.getClasses().stream().map(Classe::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Filiere toEntity(FiliereDto dto, Ecole ecole, List<Classe> classes) {
        if (dto == null) return null;
        Filiere entity = new Filiere();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNom(dto.getNom());
        entity.setEcole(ecole);
        entity.setClasses(classes);
        return entity;
    }
}