package iusjc_planning.planning_service.dto;

/**
 * DTO pour la modification manuelle d'un créneau d'emploi du temps.
 */
public class UpdateEmploiDuTempsRequest {

    private Long salleId;
    private String nomSalle;
    private String jour;
    private String heureDebut; // format HH:mm ou HH:mm:ss
    private String heureFin;   // format HH:mm ou HH:mm:ss
    private Long enseignantId;

    public UpdateEmploiDuTempsRequest() {}

    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }

    public String getNomSalle() { return nomSalle; }
    public void setNomSalle(String nomSalle) { this.nomSalle = nomSalle; }

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public Long getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Long enseignantId) { this.enseignantId = enseignantId; }
}
