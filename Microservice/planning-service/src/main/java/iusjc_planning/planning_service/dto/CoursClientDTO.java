package iusjc_planning.planning_service.dto;

import iusjc_planning.planning_service.model.StatutCours;

/**
 * DTO reçu depuis coursclasse-service pour un cours.
 * Aligné sur CoursDto de coursclasse-service.
 */
public class CoursClientDTO {

    private Long id;
    private StatutCours statutCours;
    private String nom;
    private Long classeId;
    private String codeClasse;
    private Long ueId;
    private int volumeHoraire;
    private int nbreheurefait;
    private String enseignantEmail;
    private int effectifClasse;

    public CoursClientDTO() {}

    public Long getId()                 { return id; }
    public StatutCours getStatutCours() { return statutCours; }
    public String getNom()              { return nom; }
    public Long getClasseId()           { return classeId; }
    public String getCodeClasse()       { return codeClasse; }
    public Long getUeId()               { return ueId; }
    public int getVolumeHoraire()       { return volumeHoraire; }
    public int getNbreheurefait()       { return nbreheurefait; }
    public String getEnseignantEmail()  { return enseignantEmail; }
    public int getEffectifClasse()      { return effectifClasse; }

    public void setId(Long id)                          { this.id = id; }
    public void setStatutCours(StatutCours s)           { this.statutCours = s; }
    public void setNom(String nom)                      { this.nom = nom; }
    public void setClasseId(Long classeId)              { this.classeId = classeId; }
    public void setCodeClasse(String codeClasse)        { this.codeClasse = codeClasse; }
    public void setUeId(Long ueId)                      { this.ueId = ueId; }
    public void setVolumeHoraire(int v)                 { this.volumeHoraire = v; }
    public void setNbreheurefait(int n)                 { this.nbreheurefait = n; }
    public void setEnseignantEmail(String email)        { this.enseignantEmail = email; }
    public void setEffectifClasse(int effectif)         { this.effectifClasse = effectif; }
}
