package org.example.coursclasseservice.model;

import jakarta.persistence.*;
import org.example.coursclasseservice.model.Enumeration.Sexe;
import java.util.List;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "Etudiants")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Sexe sex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idclasse")
    private Classe classe;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "etudiant_groupe",
            joinColumns = @JoinColumn(name = "etudiant_id"),
            inverseJoinColumns = @JoinColumn(name = "groupe_id"))
    private List<GroupeEtudiant> groupes;


    public Etudiant() {}

    public Long getId()                      { return id; }
    public String getMatricule()             { return matricule; }
    public String getNom()                   { return nom; }
    public String getPrenom()                { return prenom; }
    public Sexe getSex()                     { return sex; }
    public Classe getClasse()                { return classe; }
    public List<GroupeEtudiant> getGroupes() { return groupes; }

    public void setId(Long id)                          { this.id = id; }
    public void setMatricule(String matricule)           { this.matricule = matricule; }
    public void setNom(String nom)                      { this.nom = nom; }
    public void setPrenom(String prenom)                { this.prenom = prenom; }
    public void setSex(Sexe sex)                        { this.sex = sex; }
    public void setClasse(Classe classe)                { this.classe = classe; }
    public void setGroupes(List<GroupeEtudiant> groupes){ this.groupes = groupes; }
}
