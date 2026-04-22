package iusjc_planning.user_service.dto;

import lombok.Data;
import java.util.Set;

@Data
public class AuthUserDTO {
    private Long id;
    private String email;
    private String password; // hashé (BCrypt)
    private boolean mustChangePassword;
    private String statut; // "ACTIF", "INACTIF", "SUSPENDU"
    private String role; // ex: ["ROLE_ADMIN", "ROLE_ENSEIGNANT"]
}
