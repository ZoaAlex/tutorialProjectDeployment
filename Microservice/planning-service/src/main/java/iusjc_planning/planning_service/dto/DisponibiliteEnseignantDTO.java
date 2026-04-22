package iusjc_planning.planning_service.dto;

import iusjc_planning.planning_service.model.JourSemaine;
import iusjc_planning.planning_service.model.TypeDisponibilite;

import java.time.LocalTime;

public class DisponibiliteEnseignantDTO {

    private Long id;
    private JourSemaine jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeDisponibilite type;
    private String commentaire;
    private Boolean estDisponible;
    private Long enseignantId;

    public DisponibiliteEnseignantDTO() {}

    public DisponibiliteEnseignantDTO(Long id, JourSemaine jour, LocalTime heureDebut, LocalTime heureFin, TypeDisponibilite type, String commentaire, Boolean estDisponible, Long enseignantId) {
        this.id = id; this.jour = jour; this.heureDebut = heureDebut; this.heureFin = heureFin;
        this.type = type; this.commentaire = commentaire; this.estDisponible = estDisponible; this.enseignantId = enseignantId;
    }

    public Long getId() { return id; }
    public JourSemaine getJour() { return jour; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public TypeDisponibilite getType() { return type; }
    public String getCommentaire() { return commentaire; }
    public Boolean getEstDisponible() { return estDisponible; }
    public Long getEnseignantId() { return enseignantId; }

    public void setId(Long id) { this.id = id; }
    public void setJour(JourSemaine jour) { this.jour = jour; }
    public void setHeureDebut(LocalTime h) { this.heureDebut = h; }
    public void setHeureFin(LocalTime h) { this.heureFin = h; }
    public void setType(TypeDisponibilite t) { this.type = t; }
    public void setCommentaire(String c) { this.commentaire = c; }
    public void setEstDisponible(Boolean e) { this.estDisponible = e; }
    public void setEnseignantId(Long e) { this.enseignantId = e; }
}
