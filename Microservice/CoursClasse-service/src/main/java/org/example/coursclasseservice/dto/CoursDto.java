package org.example.coursclasseservice.dto;

import org.example.coursclasseservice.model.Enumeration.StatutCours;

/**
 * DTO pour un cours — record Java (accesseurs sans get).
 * effectifClasse est inclus pour que planning-service puisse
 * choisir une salle adaptée sans appel supplémentaire.
 */
public record CoursDto(
        Long id,
        StatutCours statutCours,
        String nom,
        Long classeId,
        String codeClasse,
        Long ueId,
        int volumeHoraire,
        int nbreheurefait,
        String enseignantEmail,
        int effectifClasse
) {}
