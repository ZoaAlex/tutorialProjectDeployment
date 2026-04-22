package iusjc_planning.user_service.dto;

import lombok.Data;

@Data
public class CreateEnseignantRequest {

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String specialite;
    private String grade;
}
