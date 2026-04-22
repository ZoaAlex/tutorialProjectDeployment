package iusjc_planning.user_service.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
}

