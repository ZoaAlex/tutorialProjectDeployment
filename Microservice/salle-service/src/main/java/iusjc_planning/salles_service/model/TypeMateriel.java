package iusjc_planning.salles_service.model;

/**
 * Énumération représentant les types de matériel
 */
public enum TypeMateriel {
    PROJECTEUR("Projecteur"),
    ORDINATEUR("Ordinateur"),
    TABLEAU_INTERACTIF("Tableau interactif"),
    MICRO("Microphone"),
    HAUT_PARLEUR("Haut-parleur"),
    CAMERA("Caméra"),
    ECRAN("Écran"),
    CLIMATISATION("Climatisation"),
    WIFI("WiFi"),
    AUTRE("Autre");

    private final String libelle;

    TypeMateriel(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}