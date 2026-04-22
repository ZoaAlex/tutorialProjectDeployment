package org.example.coursclasseservice.dto;

/** Sans Lombok (incompatible Java 21). */
public class SalleAttributCoursdto {
    private Long id;
    private String codeSalle;
    private String nom;
    private Integer capacite;
    private String typeSalle; // String or Enum
    private String statut;    // String or Enum
    private String description;
    private String emplacement;
    private Integer etage;
    private String batiment;

    public SalleAttributCoursdto() {}

    public Long getId()                { return id; }
    public String getCodeSalle()       { return codeSalle; }
    public String getNom()             { return nom; }
    public Integer getCapacite()       { return capacite; }
    public String getTypeSalle()       { return typeSalle; }
    public String getStatut()          { return statut; }
    public String getDescription()     { return description; }
    public String getEmplacement()     { return emplacement; }
    public Integer getEtage()          { return etage; }
    public String getBatiment()        { return batiment; }

    public void setId(Long id)                   { this.id = id; }
    public void setCodeSalle(String c)           { this.codeSalle = c; }
    public void setNom(String nom)               { this.nom = nom; }
    public void setCapacite(Integer c)           { this.capacite = c; }
    public void setTypeSalle(String t)           { this.typeSalle = t; }
    public void setStatut(String s)              { this.statut = s; }
    public void setDescription(String d)         { this.description = d; }
    public void setEmplacement(String e)         { this.emplacement = e; }
    public void setEtage(Integer e)              { this.etage = e; }
    public void setBatiment(String b)            { this.batiment = b; }
}
