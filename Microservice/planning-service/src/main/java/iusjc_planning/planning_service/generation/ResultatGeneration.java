package iusjc_planning.planning_service.generation;

import iusjc_planning.planning_service.model.EmploiDuTemps;

import java.util.List;

/**
 * Résultat retourné après la génération de l'emploi du temps pour une semaine.
 */
public class ResultatGeneration {

    private final List<PlacementEffectue> placements;
    private final List<CoursNonPlace> coursNonPlaces;


    public ResultatGeneration(List<PlacementEffectue> placements, List<CoursNonPlace> coursNonPlaces) {
        this.placements = placements;
        this.coursNonPlaces = coursNonPlaces;
    }

    public List<PlacementEffectue> getPlacements()    { return this.placements; }
    public List<CoursNonPlace> getCoursNonPlaces()    { return coursNonPlaces; }
    public int getNombrePlacements()                  { return placements != null ? placements.size() : 0; }
    public int getNombreCoursNonPlaces()              { return coursNonPlaces != null ? coursNonPlaces.size() : 0; }

    // -------------------------------------------------------------------------

    /** Représente un placement réussi dans l'emploi du temps. */
    public static class PlacementEffectue {
        private final Long coursId;
        private final String nomCours;
        private final Long enseignantId;
        private final Long classeId;
        private final Long salleId;
        private final String nomSalle;
        private final String jour;
        private final String heureDebut;
        private final String heureFin;
        private final double score;

        public PlacementEffectue(Long coursId, String nomCours, Long enseignantId,
                                  Long classeId, Long salleId, String nomSalle,
                                  String jour, String heureDebut, String heureFin, double score) {
            this.coursId = coursId;
            this.nomCours = nomCours;
            this.enseignantId = enseignantId;
            this.classeId = classeId;
            this.salleId = salleId;
            this.nomSalle = nomSalle;
            this.jour = jour;
            this.heureDebut = heureDebut;
            this.heureFin = heureFin;
            this.score = score;
        }

        public Long getCoursId()      { return coursId; }
        public String getNomCours()   { return nomCours; }
        public Long getEnseignantId() { return enseignantId; }
        public Long getClasseId()     { return classeId; }
        public Long getSalleId()      { return salleId; }
        public String getNomSalle()   { return nomSalle; }
        public String getJour()       { return jour; }
        public String getHeureDebut() { return heureDebut; }
        public String getHeureFin()   { return heureFin; }
        public double getScore()      { return score; }
    }

    /** Représente un cours qui n'a pas pu être planifié cette semaine. */
    public static class CoursNonPlace {
        private final Long coursId;
        private final String nomCours;
        private final Long enseignantId;
        private final int volumeRestant;
        private final String raison;

        public CoursNonPlace(Long coursId, String nomCours, Long enseignantId,
                              int volumeRestant, String raison) {
            this.coursId = coursId;
            this.nomCours = nomCours;
            this.enseignantId = enseignantId;
            this.volumeRestant = volumeRestant;
            this.raison = raison;
        }

        public Long getCoursId()      { return coursId; }
        public String getNomCours()   { return nomCours; }
        public Long getEnseignantId() { return enseignantId; }
        public int getVolumeRestant() { return volumeRestant; }
        public String getRaison()     { return raison; }
    }
}
