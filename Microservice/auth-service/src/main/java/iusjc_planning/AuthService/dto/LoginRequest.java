package iusjc_planning.AuthService.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
