package org.example.coursclasseservice.model.Enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatutCours {
    place,
    en_attente;

    @JsonValue
    public String toJson() {
        return this.name();
    }

    @JsonCreator
    public static StatutCours fromJson(String value) {
        if (value == null) return en_attente;
        // Accepte "place", "PLACE", "en_attente", "EN_ATTENTE", "ACTIF", etc.
        for (StatutCours s : values()) {
            if (s.name().equalsIgnoreCase(value)) return s;
        }
        // Valeurs legacy du frontend (ACTIF, SUSPENDU, etc.) → en_attente par défaut
        return en_attente;
    }
}
