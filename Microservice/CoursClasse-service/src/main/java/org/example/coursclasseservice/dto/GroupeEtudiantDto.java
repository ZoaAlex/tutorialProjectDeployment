package org.example.coursclasseservice.dto;

import java.util.List;

/** Sans Lombok (incompatible Java 21). */
public class GroupeEtudiantDto {
    private Long id;
    private String nom;
    private String description;
    private int effectif;
    private List<Long> etudiantIds;
    private List<Long> classeIds;

    public GroupeEtudiantDto() {}

    public Long getId()                      { return id; }
    public String getNom()                   { return nom; }
    public String getDescription()           { return description; }
    public int getEffectif()                 { return effectif; }
    public List<Long> getEtudiantIds()       { return etudiantIds; }
    public List<Long> getClasseIds()         { return classeIds; }

    public void setId(Long id)                          { this.id = id; }
    public void setNom(String nom)                      { this.nom = nom; }
    public void setDescription(String description)      { this.description = description; }
    public void setEffectif(int effectif)               { this.effectif = effectif; }
    public void setEtudiantIds(List<Long> etudiantIds)  { this.etudiantIds = etudiantIds; }
    public void setClasseIds(List<Long> classeIds)      { this.classeIds = classeIds; }
}
