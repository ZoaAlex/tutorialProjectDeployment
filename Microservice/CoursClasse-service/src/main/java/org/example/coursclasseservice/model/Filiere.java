package org.example.coursclasseservice.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "Filiere")
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idecole", nullable = false)
    private Ecole ecole;

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Classe> classes;

    public Filiere() {}

    public Long getId()              { return id; }
    public String getCode()          { return code; }
    public String getNom()           { return nom; }
    public Ecole getEcole()          { return ecole; }
    public List<Classe> getClasses() { return classes; }

    public void setId(Long id)                  { this.id = id; }
    public void setCode(String code)            { this.code = code; }
    public void setNom(String nom)              { this.nom = nom; }
    public void setEcole(Ecole ecole)           { this.ecole = ecole; }
    public void setClasses(List<Classe> classes){ this.classes = classes; }
}
