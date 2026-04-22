package org.example.coursclasseservice.model;

import jakarta.persistence.*;
import java.util.List;

/** Sans Lombok (incompatible Java 21). */
@Entity
@Table(name = "UE")
public class Ue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeUe;

    @Column(nullable = false)
    private String intitule;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "idclasse", nullable = false)
    private Classe classe;

    @OneToMany(mappedBy = "ue", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours;

    public Ue() {}

    public Long getId()              { return id; }
    public String getCodeUe()        { return codeUe; }
    public String getIntitule()      { return intitule; }
    public Classe getClasse()        { return classe; }
    public List<Cours> getCours()    { return cours; }

    public void setId(Long id)                  { this.id = id; }
    public void setCodeUe(String codeUe)        { this.codeUe = codeUe; }
    public void setIntitule(String intitule)    { this.intitule = intitule; }
    public void setClasse(Classe classe)        { this.classe = classe; }
    public void setCours(List<Cours> cours)     { this.cours = cours; }
}
