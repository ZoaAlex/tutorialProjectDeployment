package org.example.coursclasseservice.model;

import jakarta.persistence.*;
import java.util.List;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "Ecoles")
public class Ecole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;


    @OneToMany(mappedBy = "ecole", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Filiere> filieres;


    public Ecole() {}

    public Long getId()                  { return id; }
    public String getCode()              { return code; }
    public String getNom()               { return nom; }
    public String getDescription()       { return description; }
    public List<Filiere> getFilieres()   { return filieres; }

    public void setId(Long id)                      { this.id = id; }
    public void setCode(String code)                { this.code = code; }
    public void setNom(String nom)                  { this.nom = nom; }
    public void setDescription(String description)  { this.description = description; }
    public void setFilieres(List<Filiere> filieres) { this.filieres = filieres; }
}
