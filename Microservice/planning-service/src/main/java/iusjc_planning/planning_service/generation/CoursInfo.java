package iusjc_planning.planning_service.generation;

/**
 * Représentation locale d'un cours pendant la génération.
 * Contient les données récupérées depuis coursclasse-service
 * plus le volume restant calculé dynamiquement.
 * Sans Lombok (incompatible Java 21 dans cet environnement).
 */
public class CoursInfo {

    private final Long coursId;
    private final String nom;
    private final int volumeHoraire;
    private int nbreheurefait;
    private final Long enseignantId;
    private final Long classeId;
    private final int effectifClasse;
    private int volumeDisponibilite; // Nombre d'heures de disponibilité de l'enseignant

    public CoursInfo(Long coursId, String nom, int volumeHoraire, int nbreheurefait,
                     Long enseignantId, Long classeId, int effectifClasse, int volumeDisponibilite) {
        this.coursId = coursId;
        this.nom = nom;
        this.volumeHoraire = volumeHoraire;
        this.nbreheurefait = nbreheurefait;
        this.enseignantId = enseignantId;
        this.classeId = classeId;
        this.effectifClasse = effectifClasse;
        this.volumeDisponibilite = volumeDisponibilite;
    }

    public Long getCoursId()        { return coursId; }
    public String getNom()          { return nom; }
    public int getVolumeHoraire()   { return volumeHoraire; }
    public int getNbreheurefait()   { return nbreheurefait; }
    public Long getEnseignantId()   { return enseignantId; }
    public Long getClasseId()       { return classeId; }
    public int getEffectifClasse()  { return effectifClasse; }
    public int getVolumeDisponibilite() { return volumeDisponibilite; }
    public void setVolumeDisponibilite(int volume) { this.volumeDisponibilite = volume; }

    /** Volume horaire restant à planifier (toujours multiple de 2). */
    public int getVolumeRestant() {
        return volumeHoraire - nbreheurefait;
    }

    /** Vrai si ce cours est entièrement planifié. */
    public boolean estTermine() {
        return getVolumeRestant() <= 0;
    }

    /** Incrémente les heures effectuées de 2h après un placement. */
    public void enregistrerPlacement() {
        this.nbreheurefait += 2;
    }
}
