package iusjc_planning.planning_service.generation;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Calcule le score d'un créneau candidat (cours, créneau, salle).
 *
 * Formule :
 *   score = scoreVolume      * 0.40   (priorité volume restant faible)
 *         + scoreConcurrence * 0.30   (pression des autres enseignants sur ce créneau)
 *         + scoreSalle       * 0.20   (taux de remplissage de la salle)
 *         + scoreFragmentation * 0.10 (regroupement des cours sur les mêmes jours)
 *
 * Chaque composante est normalisée entre 0 et 1.
 */
@Component
public class ScoringService {

    // Poids des composantes du score
    private static final double POIDS_VOLUME       = 0.40;
    private static final double POIDS_CONCURRENCE  = 0.30;
    private static final double POIDS_SALLE        = 0.20;
    private static final double POIDS_FRAGMENTATION = 0.10;

    /**
     * Calcule le score d'un candidat.
     *
     * @param candidat          le candidat à scorer
     * @param tousLesCandidats  tous les candidats de la semaine (pour calculer la concurrence)
     * @param grille            la grille hebdomadaire (pour le score de fragmentation)
     * @return score entre 0 et 1
     */
    public double calculerScore(CreneauCandidat candidat,
                                List<CreneauCandidat> tousLesCandidats,
                                GrilleHebdomadaire grille) {

        double scoreVolume       = calculerScoreVolume(candidat.getCours());
        double scoreConcurrence  = calculerScoreConcurrence(candidat, tousLesCandidats);
        double scoreSalle        = calculerScoreSalle(candidat);
        double scoreFragmentation = calculerScoreFragmentation(candidat, grille);

        return scoreVolume       * POIDS_VOLUME
             + scoreConcurrence  * POIDS_CONCURRENCE
             + scoreSalle        * POIDS_SALLE
             + scoreFragmentation * POIDS_FRAGMENTATION;
    }

    /**
     * Score basé sur le volume de disponibilité restant de l'enseignant.
     * Plus le volume est faible, plus le score est élevé.
     * Formule : 2.0 / volumeDisponibilite (normalisé entre 0 et 1)
     */
    private double calculerScoreVolume(CoursInfo cours) {
        int volumeDispo = cours.getVolumeDisponibilite();
        if (volumeDispo <= 0) return 1.0; // Score maximal si plus aucune dispo (urgence)
        // 2h restantes → score = 1.0 ; 4h → 0.5 ; 8h → 0.25 ; 20h → 0.1
        return Math.min(1.0, 2.0 / volumeDispo);
    }

    /**
     * Score de concurrence : mesure la pression des autres enseignants sur ce créneau.
     *
     * Si plusieurs enseignants sont disponibles sur le même créneau, le cours avec
     * le moins de volume restant doit passer en premier (son scoreVolume est déjà élevé).
     * Ce score amplifie la différenciation en cas de concurrence.
     *
     * Formule : nombre d'enseignants concurrents sur ce créneau / total enseignants dispo semaine
     */
    private double calculerScoreConcurrence(CreneauCandidat candidat,
                                            List<CreneauCandidat> tousLesCandidats) {
        CreneauHoraire creneau = candidat.getCreneau();
        Long enseignantId = candidat.getCours().getEnseignantId();

        // Compter les autres enseignants disponibles sur ce même créneau
        long concurrents = tousLesCandidats.stream()
                .filter(c -> c.getCreneau().equals(creneau))
                .map(c -> c.getCours().getEnseignantId())
                .filter(id -> !id.equals(enseignantId))
                .distinct()
                .count();

        // Total d'enseignants distincts disponibles sur la semaine
        long totalEnseignants = tousLesCandidats.stream()
                .map(c -> c.getCours().getEnseignantId())
                .distinct()
                .count();

        if (totalEnseignants <= 1) return 0.0;
        return (double) concurrents / (totalEnseignants - 1);
    }

    /**
     * Score basé sur le taux de remplissage de la salle.
     * Une salle dont la capacité est proche de l'effectif de la classe est préférée.
     */
    private double calculerScoreSalle(CreneauCandidat candidat) {
        return candidat.getSalle().tauxRemplissage(candidat.getCours().getEffectifClasse());
    }

    /**
     * Score de fragmentation : favorise les créneaux sur des jours déjà utilisés par la classe.
     * Évite d'ouvrir un nouveau jour si des créneaux sont encore disponibles sur les jours existants.
     *
     * Formule : coursDejaPlacesCeJour / nombreCreneauxParJour
     */
    private double calculerScoreFragmentation(CreneauCandidat candidat, GrilleHebdomadaire grille) {
        int coursDejaPlaces = grille.getNombreCoursPlacesCeJour(
                candidat.getCours().getClasseId(),
                candidat.getCreneau().getJour()
        );
        return (double) coursDejaPlaces / grille.getNombreCreneauxParJour();
    }
}
