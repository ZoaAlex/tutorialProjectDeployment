package org.example.coursclasseservice.dto;

import org.example.coursclasseservice.model.Enumeration.Sexe;
import java.util.List;

/** Sans Lombok (incompatible Java 21). */
public class EtudiantDto {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private Sexe sex;
    private Long classeId;
    private List<Long> groupeIds;

    public EtudiantDto() {}

    public Long getId()                  { return id; }
    public String getMatricule()         { return matricule; }
    public String getNom()               { return nom; }
    public String getPrenom()            { return prenom; }
    public Sexe getSex()                 { return sex; }
    public Long getClasseId()            { return classeId; }
    public List<Long> getGroupeIds()     { return groupeIds; }

    public void setId(Long id)                      { this.id = id; }
    public void setMatricule(String matricule)       { this.matricule = matricule; }
    public void setNom(String nom)                  { this.nom = nom; }
    public void setPrenom(String prenom)            { this.prenom = prenom; }
    public void setSex(Sexe sex)                    { this.sex = sex; }
    public void setClasseId(Long classeId)          { this.classeId = classeId; }
    public void setGroupeIds(List<Long> groupeIds)  { this.groupeIds = groupeIds; }
}
