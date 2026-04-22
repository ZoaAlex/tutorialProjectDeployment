package org.example.coursclasseservice.model;

import jakarta.persistence.*;
import java.util.List;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "Classes")
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long salleId;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String code;

    @Column
    private int effectif;


    @OneToMany(mappedBy = "classe", fetch = FetchType.LAZY)
    private List<Etudiant> etudiants;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfiliere", nullable = false)
    private Filiere filiere;

    public Classe() {}

    public Long getId()                  { return id; }
    public String getNom()               { return nom; }
    public String getCode()              { return code; }
    public int getEffectif()             { return effectif; }
    public Long getSalleId()             { return salleId; }
    public List<Etudiant> getEtudiants() { return etudiants; }
    public Filiere getFiliere()          { return filiere; }

    public void setId(Long id)                      { this.id = id; }
    public void setNom(String nom)                  { this.nom = nom; }
    public void setCode(String code)                { this.code = code; }
    public void setEffectif(int effectif)           { this.effectif = effectif; }
    public void setSalleId(Long salleId)            { this.salleId = salleId; }
    public void setEtudiants(List<Etudiant> e)      { this.etudiants = e; }
    public void setFiliere(Filiere filiere)         { this.filiere = filiere; }
}
