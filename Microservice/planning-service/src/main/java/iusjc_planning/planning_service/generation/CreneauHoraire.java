package iusjc_planning.planning_service.generation;

import iusjc_planning.planning_service.model.JourSemaine;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Représente un créneau horaire fixe de 2h dans la grille hebdomadaire.
 * Les créneaux sont : 8h-10h, 10h-12h, 13h-15h, 15h-17h (pause 12h-13h exclue).
 * Sans Lombok (incompatible Java 21 dans cet environnement).
 */
public class CreneauHoraire {

    private final JourSemaine jour;
    private final LocalTime heureDebut;
    private final LocalTime heureFin;

    // Créneaux fixes de la journée (pause 12h-13h exclue)
    public static final LocalTime[] DEBUTS = {
        LocalTime.of(8, 0),
        LocalTime.of(10, 0),
        LocalTime.of(13, 0),
        LocalTime.of(15, 0)
    };

    public static final LocalTime[] FINS = {
        LocalTime.of(10, 0),
        LocalTime.of(12, 0),
        LocalTime.of(15, 0),
        LocalTime.of(17, 0)
    };

    public CreneauHoraire(JourSemaine jour, LocalTime heureDebut, LocalTime heureFin) {
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public JourSemaine getJour()      { return jour; }
    public LocalTime getHeureDebut()  { return heureDebut; }
    public LocalTime getHeureFin()    { return heureFin; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreneauHoraire)) return false;
        CreneauHoraire that = (CreneauHoraire) o;
        return Objects.equals(jour, that.jour)
            && Objects.equals(heureDebut, that.heureDebut)
            && Objects.equals(heureFin, that.heureFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jour, heureDebut, heureFin);
    }

    @Override
    public String toString() {
        return jour.getNom() + " " + heureDebut + "-" + heureFin;
    }
}
