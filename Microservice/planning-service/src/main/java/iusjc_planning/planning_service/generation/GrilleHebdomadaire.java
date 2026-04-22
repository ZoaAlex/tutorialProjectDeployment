package iusjc_planning.planning_service.generation;

import iusjc_planning.planning_service.model.JourSemaine;

import java.util.*;

/**
 * Représente l'état de la grille horaire pour une semaine donnée.
 *
 * Grille journalière fixe (lundi → samedi) :
 *   Créneau 1 :  8h00 → 10h00
 *   Créneau 2 : 10h00 → 12h00
 *   --- PAUSE 12h-13h ---
 *   Créneau 3 : 13h00 → 15h00
 *   Créneau 4 : 15h00 → 17h00
 *
 * Suit l'état d'occupation par classe et par enseignant.
 */
public class GrilleHebdomadaire {

    // Jours ouvrés : lundi → samedi
    public static final List<JourSemaine> JOURS_OUVRES = List.of(
            JourSemaine.LUNDI, JourSemaine.MARDI, JourSemaine.MERCREDI,
            JourSemaine.JEUDI, JourSemaine.VENDREDI, JourSemaine.SAMEDI
    );

    /**
     * Tous les créneaux possibles de la semaine (6 jours × 4 créneaux = 24 créneaux).
     */
    private final List<CreneauHoraire> tousLesCreneaux;

    /**
     * Créneaux occupés par classe : classeId → ensemble de créneaux occupés.
     * Une classe ne peut avoir qu'un seul cours par créneau.
     */
    private final Map<Long, Set<CreneauHoraire>> occupationParClasse;

    /**
     * Créneaux occupés par enseignant : enseignantId → ensemble de créneaux occupés.
     * Un enseignant ne peut donner qu'un seul cours par créneau.
     */
    private final Map<Long, Set<CreneauHoraire>> occupationParEnseignant;

    /**
     * Créneaux occupés par salle : salleId → ensemble de créneaux occupés.
     */
    private final Map<Long, Set<CreneauHoraire>> occupationParSalle;

    /**
     * Nombre de cours déjà placés par (classeId, jour) pour le score de fragmentation.
     */
    private final Map<String, Integer> coursParClasseEtJour;

    public GrilleHebdomadaire() {
        this.tousLesCreneaux = construireTousLesCreneaux();
        this.occupationParClasse = new HashMap<>();
        this.occupationParEnseignant = new HashMap<>();
        this.occupationParSalle = new HashMap<>();
        this.coursParClasseEtJour = new HashMap<>();
    }

    /**
     * Construit la liste de tous les créneaux de la semaine.
     */
    private List<CreneauHoraire> construireTousLesCreneaux() {
        List<CreneauHoraire> creneaux = new ArrayList<>();
        for (JourSemaine jour : JOURS_OUVRES) {
            for (int i = 0; i < CreneauHoraire.DEBUTS.length; i++) {
                creneaux.add(new CreneauHoraire(jour, CreneauHoraire.DEBUTS[i], CreneauHoraire.FINS[i]));
            }
        }
        return Collections.unmodifiableList(creneaux);
    }

    /**
     * Vérifie si un créneau est libre pour une classe donnée.
     */
    public boolean estLibrePourClasse(Long classeId, CreneauHoraire creneau) {
        return !occupationParClasse
                .getOrDefault(classeId, Collections.emptySet())
                .contains(creneau);
    }

    /**
     * Vérifie si un créneau est libre pour un enseignant donné.
     */
    public boolean estLibrePourEnseignant(Long enseignantId, CreneauHoraire creneau) {
        return !occupationParEnseignant
                .getOrDefault(enseignantId, Collections.emptySet())
                .contains(creneau);
    }

    /**
     * Vérifie si un créneau est libre pour une salle donnée.
     */
    public boolean estLibrePourSalle(Long salleId, CreneauHoraire creneau) {
        return !occupationParSalle
                .getOrDefault(salleId, Collections.emptySet())
                .contains(creneau);
    }

    /**
     * Enregistre un placement dans la grille.
     * Met à jour l'occupation de la classe, de l'enseignant et de la salle.
     */
    public void enregistrerPlacement(Long classeId, Long enseignantId, Long salleId, CreneauHoraire creneau) {
        occupationParClasse.computeIfAbsent(classeId, k -> new HashSet<>()).add(creneau);
        occupationParEnseignant.computeIfAbsent(enseignantId, k -> new HashSet<>()).add(creneau);
        occupationParSalle.computeIfAbsent(salleId, k -> new HashSet<>()).add(creneau);

        // Mise à jour du compteur de fragmentation
        String cle = classeId + "_" + creneau.getJour().name();
        coursParClasseEtJour.merge(cle, 1, Integer::sum);
    }

    /**
     * Retourne le nombre de cours déjà placés pour une classe sur un jour donné.
     * Utilisé pour le score de fragmentation.
     */
    public int getNombreCoursPlacesCeJour(Long classeId, JourSemaine jour) {
        String cle = classeId + "_" + jour.name();
        return coursParClasseEtJour.getOrDefault(cle, 0);
    }

    /**
     * Retourne tous les créneaux de la semaine.
     */
    public List<CreneauHoraire> getTousLesCreneaux() {
        return tousLesCreneaux;
    }

    /**
     * Retourne le nombre total de créneaux par jour (4).
     */
    public int getNombreCreneauxParJour() {
        return CreneauHoraire.DEBUTS.length;
    }
}
