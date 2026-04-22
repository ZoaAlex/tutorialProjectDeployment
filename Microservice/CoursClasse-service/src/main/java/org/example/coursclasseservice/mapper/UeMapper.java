package org.example.coursclasseservice.mapper;

import org.example.coursclasseservice.dto.UeDto;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Cours;
import org.example.coursclasseservice.model.Ue;
import java.util.List;
import java.util.stream.Collectors;

public class UeMapper {

    public static UeDto toDto(Ue entity) {
        if (entity == null) return null;
        UeDto dto = new UeDto();
        dto.setId(entity.getId());
        dto.setCodeUe(entity.getCodeUe());
        dto.setIntitule(entity.getIntitule());
        dto.setClasseId(entity.getClasse() != null ? entity.getClasse().getId() : null);
        if (entity.getCours() != null) {
            dto.setCoursIds(entity.getCours().stream().map(Cours::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Ue toEntity(UeDto dto, List<Cours> cours, Classe classe) {
        if (dto == null) return null;
        Ue entity = new Ue();
        entity.setId(dto.getId());
        entity.setCodeUe(dto.getCodeUe());
        entity.setIntitule(dto.getIntitule());
        entity.setClasse(classe);
        entity.setCours(cours);
        return entity;
    }
}