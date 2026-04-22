package org.example.coursclasseservice.dto;

import java.util.List;

/** Sans Lombok (incompatible Java 21). */
public class EcoleDto {
    private Long id;
    private String code;
    private String nom;
    private String description;
    private List<Long> classeIds;
    private List<Long> filiereIds;

    public EcoleDto() {}

    public Long getId()                  { return id; }
    public String getCode()              { return code; }
    public String getNom()               { return nom; }
    public String getDescription()       { return description; }
    public List<Long> getClasseIds()     { return classeIds; }
    public List<Long> getFiliereIds()    { return filiereIds; }

    public void setId(Long id)                      { this.id = id; }
    public void setCode(String code)                { this.code = code; }
    public void setNom(String nom)                  { this.nom = nom; }
    public void setDescription(String description)  { this.description = description; }
    public void setClasseIds(List<Long> classeIds)  { this.classeIds = classeIds; }
    public void setFiliereIds(List<Long> filiereIds){ this.filiereIds = filiereIds; }
}
