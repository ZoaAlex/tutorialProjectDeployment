package iusjc_planning.planning_service.generation;

/**
 * Représente une paire (cours, créneau, salle) valide candidate au placement.
 * Un créneau est candidat si toutes les contraintes sont respectées :
 * - enseignant disponible sur ce créneau
 * - classe libre sur ce créneau
 * - enseignant libre sur ce créneau
 * - salle disponible avec capacité suffisante
 */
public class CreneauCandidat {

    private final CoursInfo cours;
    private final CreneauHoraire creneau;
    private final SalleInfo salle;
    private double score;

    public CreneauCandidat(CoursInfo cours, CreneauHoraire creneau, SalleInfo salle) {
        this.cours = cours;
        this.creneau = creneau;
        this.salle = salle;
        this.score = 0.0;
    }

    public CoursInfo getCours()       { return cours; }
    public CreneauHoraire getCreneau(){ return creneau; }
    public SalleInfo getSalle()       { return salle; }
    public double getScore()          { return score; }
    public void setScore(double score){ this.score = score; }

    @Override
    public String toString() {
        return String.format("Candidat[cours=%s, créneau=%s, salle=%s, score=%.3f]",
                cours.getNom(), creneau, salle.getNom(), score);
    }
}
