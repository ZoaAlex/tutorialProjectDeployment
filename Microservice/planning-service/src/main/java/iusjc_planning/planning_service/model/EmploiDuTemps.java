package iusjc_planning.planning_service.model;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * Entité représentant un créneau d'emploi du temps généré et persisté.
 */
@Entity
@Table(name = "emploi_du_temps")
public class EmploiDuTemps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long coursId;

    @Column(nullable = false)
    private String nomCours;

    @Column(nullable = false)
    private Long enseignantId;

    @Column(nullable = false)
    private Long classeId;

    @Column(nullable = false)
    private Long salleId;

    @Column(nullable = false)
    private String nomSalle;

    @Column(nullable = false)
    private String jour;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    public EmploiDuTemps() {}

    public EmploiDuTemps(Long coursId, String nomCours, Long enseignantId, Long classeId,
                         Long salleId, String nomSalle, String jour, LocalTime heureDebut, LocalTime heureFin) {
        this.coursId = coursId;
        this.nomCours = nomCours;
        this.enseignantId = enseignantId;
        this.classeId = classeId;
        this.salleId = salleId;
        this.nomSalle = nomSalle;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoursId() { return coursId; }
    public void setCoursId(Long coursId) { this.coursId = coursId; }

    public String getNomCours() { return nomCours; }
    public void setNomCours(String nomCours) { this.nomCours = nomCours; }

    public Long getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Long enseignantId) { this.enseignantId = enseignantId; }

    public Long getClasseId() { return classeId; }
    public void setClasseId(Long classeId) { this.classeId = classeId; }

    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }

    public String getNomSalle() { return nomSalle; }
    public void setNomSalle(String nomSalle) { this.nomSalle = nomSalle; }

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
}
