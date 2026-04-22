package iusjc_planning.salles_service.model;

/**
 * Énumération représentant les types de salle
 */
public enum TypeSalle {
    AMPHITHEATRE("Amphithéâtre"),
    SALLE_COURS("Salle de cours"),
    LABORATOIRE("Laboratoire"),
    SALLE_INFORMATIQUE("Salle informatique"),
    SALLE_CONFERENCE("Salle de conférence"),
    BIBLIOTHEQUE("Bibliothèque"),
    BUREAU("Bureau"),
    AUTRE("Autre");

    private final String libelle;

    TypeSalle(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}