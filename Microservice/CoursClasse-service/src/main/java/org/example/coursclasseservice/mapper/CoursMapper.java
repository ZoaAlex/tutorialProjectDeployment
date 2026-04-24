package org.example.coursclasseservice.mapper;

import java.util.Objects;

import org.example.coursclasseservice.dto.CoursDto;
import org.example.coursclasseservice.model.Cours;
import org.example.coursclasseservice.model.Ue;

/**
 * Mapper pour Cours <-> CoursDto.
 * CoursDto est un record Java : accesseurs sans "get" (ex: dto.id(), dto.classeId()).
 */
public class CoursMapper {

    public static CoursDto toDto(Cours entity) {
        if (entity == null) return null;
        // enseignant est un long primitif dans Cours → on le convertit en Long nullable
        // (0 signifie "pas d'enseignant assigné", on retourne null dans ce cas)
        String enseignantEmail = !Objects.equals(entity.getEnseignant(), "") ? entity.getEnseignant() : null;
        Long classeId = null;
        String codeClasse = null;
        int effectifClasse = 0;
        if (entity.getUe() != null && entity.getUe().getClasse() != null) {
            classeId = entity.getUe().getClasse().getId();
            codeClasse = entity.getUe().getClasse().getCode();
            effectifClasse = entity.getUe().getClasse().getEffectif();
            return new CoursDto(
                    entity.getId(),
                    entity.getStatutCours(),
                    entity.getNom(),
                    classeId,
                    codeClasse,
                    entity.getUe() != null ? entity.getUe().getId() : null,
                    entity.getVolumeHoraire(),
                    entity.getNbreheurefait(),
                    enseignantEmail,
                    effectifClasse
            );
        } else {
            return new CoursDto(
                    entity.getId(),
                    entity.getStatutCours(),
                    entity.getNom(),
                    classeId,
                    codeClasse,
                    entity.getUe() != null ? entity.getUe().getId() : null,
                    entity.getVolumeHoraire(),
                    entity.getNbreheurefait(),
                    enseignantEmail,
                    effectifClasse

            );
        }

    }

    public static Cours toEntity(CoursDto dto, Ue ue) {
        if (dto == null) return null;
        Cours entity = new Cours();
        entity.setId(dto.id());
        entity.setStatutCours(dto.statutCours() != null ? dto.statutCours() : org.example.coursclasseservice.model.Enumeration.StatutCours.en_attente);
        entity.setNbreheurefait(dto.nbreheurefait());
        entity.setVolumeHoraire(dto.volumeHoraire());
        entity.setCodeSalle(dto.codeClasse());
        entity.setUe(ue);
        entity.setEnseignantemail(dto.enseignantEmail());
        entity.setNom(dto.nom());
        return entity;
    }
}
