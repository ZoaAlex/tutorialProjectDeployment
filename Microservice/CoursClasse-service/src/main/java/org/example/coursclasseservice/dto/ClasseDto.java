package org.example.coursclasseservice.dto;

import java.util.List;

/** Sans Lombok (incompatible Java 21). */
public class ClasseDto {
    private Long id;
    private String code;
    private String nom;
    private String specialite;
    private int effectif;
    private Long filiereId;
    private Long salleId;
    private String codeSalle;
    private List<Long> etudiantIds;

    public ClasseDto() {}

    public Long getId()                      { return id; }
    public String getCode()                  { return code; }
    public String getNom()                   { return nom; }
    public String getSpecialite()            { return specialite; }
    public int getEffectif()                 { return effectif; }
    public Long getFiliereId()               { return filiereId; }
    public Long getSalleId()                 { return salleId; }
    public String getCodeSalle()             { return codeSalle; }
    public List<Long> getEtudiantIds()       { return etudiantIds; }

    public void setId(Long id)                          { this.id = id; }
    public void setCode(String code)                    { this.code = code; }
    public void setNom(String nom)                      { this.nom = nom; }
    public void setSpecialite(String specialite)        { this.specialite = specialite; }
    public void setEffectif(int effectif)               { this.effectif = effectif; }
    public void setFiliereId(Long filiereId)            { this.filiereId = filiereId; }
    public void setSalleId(Long salleId)                { this.salleId = salleId; }
    public void setCodeSalle(String codeSalle)          { this.codeSalle = codeSalle; }
    public void setEtudiantIds(List<Long> etudiantIds)  { this.etudiantIds = etudiantIds; }
}
