package iusjc_planning.user_service.dto;

import lombok.Data;

import java.util.Set;

@Data
public class EnseignantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;

    private String specialite;
    private String grade;
    private String password; // Mot de passe en clair (uniquement au moment de la création)
    private String message; // Message de confirmation avec les détails du compte

    private String role;
}
