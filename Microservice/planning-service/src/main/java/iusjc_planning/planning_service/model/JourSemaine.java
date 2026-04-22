package iusjc_planning.planning_service.model;

public enum JourSemaine {
    LUNDI("Lundi"),
    MARDI("Mardi"),
    MERCREDI("Mercredi"),
    JEUDI("Jeudi"),
    VENDREDI("Vendredi"),
    SAMEDI("Samedi");

    private final String nom;

    JourSemaine(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
}