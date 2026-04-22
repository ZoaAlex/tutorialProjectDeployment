package iusjc_planning.salles_service.model;

/**
 * Énumération représentant le statut d'une réservation
 */
public enum StatutReservation {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    REJETEE("Rejetée"),
    ANNULEE("Annulée"),
    TERMINEE("Terminée");

    private final String libelle;

    StatutReservation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}