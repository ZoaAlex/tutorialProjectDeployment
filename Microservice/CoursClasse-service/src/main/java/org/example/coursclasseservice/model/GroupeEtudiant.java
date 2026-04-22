package org.example.coursclasseservice.model;

import jakarta.persistence.*;
import java.util.List;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "GroupeEtudiant")
public class GroupeEtudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column
    private String description;

    @Column
    private int effectif;

    @ManyToMany(mappedBy = "groupes", fetch = FetchType.LAZY)
    private List<Etudiant> etudiants;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "groupe_classe",
            joinColumns = @JoinColumn(name = "groupe_id"),
            inverseJoinColumns = @JoinColumn(name = "classe_id"))
    private List<Classe> classes;

    public GroupeEtudiant() {}

    public Long getId()                      { return id; }
    public String getNom()                   { return nom; }
    public String getDescription()           { return description; }
    public int getEffectif()                 { return effectif; }
    public List<Etudiant> getEtudiants()     { return etudiants; }
    public List<Classe> getClasses()         { return classes; }

    public void setId(Long id)                          { this.id = id; }
    public void setNom(String nom)                      { this.nom = nom; }
    public void setDescription(String description)      { this.description = description; }
    public void setEffectif(int effectif)               { this.effectif = effectif; }
    public void setEtudiants(List<Etudiant> etudiants)  { this.etudiants = etudiants; }
    public void setClasses(List<Classe> classes)        { this.classes = classes; }
}
