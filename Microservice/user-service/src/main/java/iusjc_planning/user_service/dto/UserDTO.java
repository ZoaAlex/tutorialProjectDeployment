package iusjc_planning.user_service.dto;

import iusjc_planning.user_service.model.StatutUser;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private StatutUser statut;
    private String role; // "ROLE_ADMIN" ou "ROLE_ENSEIGNANT"
    private boolean mustChangePassword;

}
