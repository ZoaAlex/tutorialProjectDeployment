package iusjc_planning.user_service.dto;

import lombok.Data;

@Data
public class EnseignantDTO extends UserDTO {

    private String specialite;
    private String grade;
}

