package org.example.coursclasseservice.model;

import jakarta.persistence.*;
import org.example.coursclasseservice.model.Enumeration.StatutCours;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "Cours")
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCours statutCours;

    private String codeSalle;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idue")
    private Ue ue;

    @Column(nullable = false)
    private int volumeHoraire;

    @Column(nullable = false, name = "heurefait")
    private int nbreheurefait;

    private String enseignantemail;


    public Cours() {}

    public Long getId()                  { return id; }
    public String getNom()               { return nom; }
    public StatutCours getStatutCours()  { return statutCours; }
    public Ue getUe()                    { return ue; }
    public int getVolumeHoraire()        { return volumeHoraire; }
    public int getNbreheurefait()        { return nbreheurefait; }
    public String getEnseignant()          { return enseignantemail; }
    public String getCodeSalle() { return codeSalle; }

    public void setId(Long id)                      { this.id = id; }
    public void setNom(String nom)                  { this.nom = nom; }
    public void setStatutCours(StatutCours s)       { this.statutCours = s; }
    public void setUe(Ue ue)                        { this.ue = ue; }
    public void setVolumeHoraire(int v)             { this.volumeHoraire = v; }

    public void setCodeSalle(String codeSalle) {this.codeSalle = codeSalle;}

    public void setNbreheurefait(int n)             { this.nbreheurefait = n; }
    public void setEnseignantemail(String mail)      { this.enseignantemail = mail; }
}
