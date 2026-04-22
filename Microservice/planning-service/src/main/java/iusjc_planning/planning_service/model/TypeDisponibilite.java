package iusjc_planning.planning_service.model;

public enum TypeDisponibilite {
    MATIN("08:00-12:00"),
    APRES_MIDI("13:00-17:00"),
    SOIR("18:00-22:00"),
    JOURNEE_COMPLETE("08:00-17:00"),
    PERSONNALISE("Horaire personnalisé");

    private final String description;

    TypeDisponibilite(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}