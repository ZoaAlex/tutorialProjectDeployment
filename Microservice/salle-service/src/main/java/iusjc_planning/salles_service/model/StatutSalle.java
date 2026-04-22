package iusjc_planning.salles_service.model;

/**
 * Énumération représentant le statut d'une salle
 * Basée sur le diagramme de classe
 */
public enum StatutSalle {
    OCCUPEE("Occupée"),
    LIBRE("Libre"),
    MAINTENANCE("En maintenance"),
    HORS_SERVICE("Hors service");

    private final String libelle;

    StatutSalle(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}