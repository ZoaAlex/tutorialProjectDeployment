package iusjc_planning.AuthService.feign;

import iusjc_planning.AuthService.dto.AuthUserDTO;
import iusjc_planning.AuthService.dto.ChangePasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}"  // défini dans application.properties
)
public interface UserClient {

    /** Récupérer un user selon son email (utilisé pour login) */
    @GetMapping("/api/users/auth/by-email")
    AuthUserDTO getByEmail(@RequestParam("email") String email);

    /** Récupérer un user selon son ID */
    @GetMapping("/api/users/{id}")
    AuthUserDTO getById(@PathVariable("id") Long id);

    /** Vérifier si un email existe */
    @GetMapping("/api/users/exists")
    boolean existsByEmail(@RequestParam("email") String email);

    /** Mise à jour du mot de passe → UserService */
    @PutMapping("/api/users/{id}/update-password")
    void updatePassword(
            @PathVariable("id") Long id,
            @RequestBody ChangePasswordRequest request
    );

    /** mustChangePassword = false (fin de première connexion) */
    @PutMapping("/api/users/{id}/mark-password-changed")
    void markPasswordChanged(@PathVariable("id") Long id);
}
