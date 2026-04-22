package iusjc_planning.user_service.mapper;

import iusjc_planning.user_service.dto.AuthUserDTO;
import iusjc_planning.user_service.dto.UserDTO;
import iusjc_planning.user_service.dto.UserResponse;
import iusjc_planning.user_service.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDto(User user) {
        if (user == null)
            return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setStatut(user.getStatut());
        dto.setMustChangePassword(user.isMustChangePassword());

        dto.setRole(user.getRole());

        return dto;
    }

    /**
     * Conversion vers AuthUserDTO (inclut le mot de passe hashé)
     * Utilisé uniquement pour l'authentification
     */
    public static AuthUserDTO toAuthDto(User user) {
        if (user == null)
            return null;

        AuthUserDTO dto = new AuthUserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword()); // mot de passe hashé
        dto.setMustChangePassword(user.isMustChangePassword());
        dto.setStatut(user.getStatut() != null ? user.getStatut().name() : null);

        dto.setRole(user.getRole());

        return dto;
    }

    public static UserResponse toResponse(User user) {
        if (user == null)
            return null;

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setNom(user.getNom());
        res.setPrenom(user.getPrenom());
        res.setEmail(user.getEmail());

        res.setRole(user.getRole());
        return res;
    }
}
