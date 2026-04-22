package iusjc_planning.planning_service.generation;

/**
 * Représentation locale d'une salle pendant la génération.
 * Données récupérées depuis salles-service.
 */
public class SalleInfo {

    private final Long salleId;
    private final String nom;
    private final int capacite;

    public SalleInfo(Long salleId, String nom, int capacite) {
        this.salleId = salleId;
        this.nom = nom;
        this.capacite = capacite;
    }

    public Long getSalleId()  { return salleId; }
    public String getNom()    { return nom; }
    public int getCapacite()  { return capacite; }

    /**
     * Taux de remplissage pour un effectif donné (entre 0 et 1).
     * Retourne 0 si l'effectif dépasse la capacité (salle inadaptée).
     */
    public double tauxRemplissage(int effectif) {
        if (effectif > capacite) return 0.0;
        return (double) effectif / capacite;
    }
}
