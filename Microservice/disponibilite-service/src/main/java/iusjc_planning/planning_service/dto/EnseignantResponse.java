package iusjc_planning.planning_service.dto;

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
    private Set<String> roles;
}
