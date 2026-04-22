package iusjc_planning.planning_service.dto;

 /* DTO de réponse pour un enseignant (reçu depuis user-service).
 * Sans Lombok (incompatible Java 21 dans cet environnement).
 */
public class EnseignantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String specialite;
    private String grade;
    private String role;

    public EnseignantResponse() {}

    public Long getId()           { return id; }
    public String getNom()        { return nom; }
    public String getPrenom()     { return prenom; }
    public String getEmail()      { return email; }
    public String getSpecialite() { return specialite; }
    public String getGrade()      { return grade; }
    public String getRole()       { return role; }

    public void setId(Long id)              { this.id = id; }
    public void setNom(String nom)          { this.nom = nom; }
    public void setPrenom(String prenom)    { this.prenom = prenom; }
    public void setEmail(String email)      { this.email = email; }
    public void setSpecialite(String s)     { this.specialite = s; }
    public void setGrade(String grade)      { this.grade = grade; }
    public void setRole(String role)        { this.role = role; }
}
