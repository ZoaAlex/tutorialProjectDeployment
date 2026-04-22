package iusjc_planning.planning_service.dto;

/**
 * DTO reçu depuis coursclasse-service pour une classe.
 * Aligné sur ClasseDto de coursclasse-service.
 */
public class ClasseResponse {

    private Long id;
    private String code;
    private String nom;
    private String specialite;
    private int effectif;
    private Long filiereId;
    private Long salleId;
    private String codeSalle;

    public ClasseResponse() {}

    public Long getId()          { return id; }
    public String getCode()      { return code; }
    public String getNom()       { return nom; }
    public String getSpecialite(){ return specialite; }
    public int getEffectif()     { return effectif; }
    public Long getFiliereId()   { return filiereId; }
    public Long getSalleId()     { return salleId; }
    public String getCodeSalle() { return codeSalle; }

    public void setId(Long id)                  { this.id = id; }
    public void setCode(String code)            { this.code = code; }
    public void setNom(String nom)              { this.nom = nom; }
    public void setSpecialite(String s)         { this.specialite = s; }
    public void setEffectif(int effectif)       { this.effectif = effectif; }
    public void setFiliereId(Long filiereId)    { this.filiereId = filiereId; }
    public void setSalleId(Long salleId)        { this.salleId = salleId; }
    public void setCodeSalle(String codeSalle)  { this.codeSalle = codeSalle; }
}
