package iusjc_planning.salles_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la réponse de disponibilité des salles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteResponse {
    private Long salleId;
    private String codeSalle;
    private String nomSalle;
    private Integer capacite;
    private Boolean disponible;
    private List<CreneauOccupe> creneauxOccupes;
    private List<CreneauLibre> creneauxLibres;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreneauOccupe {
        private LocalDateTime debut;
        private LocalDateTime fin;
        private String motif;
        private String typeOccupation; // RESERVATION, COURS, MAINTENANCE
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreneauLibre {
        private LocalDateTime debut;
        private LocalDateTime fin;
        private Long dureeEnMinutes;
    }
}