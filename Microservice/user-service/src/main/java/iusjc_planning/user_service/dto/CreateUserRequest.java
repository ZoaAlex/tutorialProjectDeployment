package iusjc_planning.user_service.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CreateUserRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password; // Raw password
    private String role; // e.g. "ENSEIGNANT", "ADMIN"

    // Fields specific to Enseignant
    private String specialite;
    private String grade;
}
