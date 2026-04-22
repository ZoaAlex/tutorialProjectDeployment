package iusjc_planning.planning_service.dto;

/**
 * DTO reçu depuis salles-service pour une salle.
 * Sans Lombok (incompatible Java 21 dans cet environnement).
 */
public class SalleClientDTO {

    private Long id;
    private String codeSalle;
    private String nom;
    private Integer capacite;

    public SalleClientDTO() {}

    public SalleClientDTO(Long id, String codeSalle, String nom, Integer capacite) {
        this.id = id;
        this.codeSalle = codeSalle;
        this.nom = nom;
        this.capacite = capacite;
    }

    public Long getId()          { return id; }
    public String getCodeSalle() { return codeSalle; }
    public String getNom()       { return nom; }
    public Integer getCapacite() { return capacite; }

    public void setId(Long id)                { this.id = id; }
    public void setCodeSalle(String c)        { this.codeSalle = c; }
    public void setNom(String nom)            { this.nom = nom; }
    public void setCapacite(Integer c)        { this.capacite = c; }
}
