package org.example.coursclasseservice.dto;

import java.util.List;

/** Sans Lombok (incompatible Java 21). */
public class FiliereDto {
    private Long id;
    private String code;
    private String nom;
    private Long ecoleId;
    private List<Long> classeIds;

    public FiliereDto() {}

    public Long getId()                  { return id; }
    public String getCode()              { return code; }
    public String getNom()               { return nom; }
    public Long getEcoleId()             { return ecoleId; }
    public List<Long> getClasseIds()     { return classeIds; }

    public void setId(Long id)                      { this.id = id; }
    public void setCode(String code)                { this.code = code; }
    public void setNom(String nom)                  { this.nom = nom; }
    public void setEcoleId(Long ecoleId)            { this.ecoleId = ecoleId; }
    public void setClasseIds(List<Long> classeIds)  { this.classeIds = classeIds; }
}