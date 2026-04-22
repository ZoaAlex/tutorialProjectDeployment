package iusjc_planning.user_service.controller;

import iusjc_planning.user_service.dto.*;
//import iusjc_planning.user_service.dto.ChangePasswordRequest;
import iusjc_planning.user_service.mapper.UserMapper;
import iusjc_planning.user_service.model.User;
import iusjc_planning.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Request to get all users");
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserMapper::toDto)
                .toList();
        log.info("Found {} users", users.size());
        return users;
    }

    /**
     * Endpoint pour l'authentification (inclut le mot de passe hashé)
     * Utilisé uniquement par auth-service
     */
    @GetMapping("/auth/by-email")
    public AuthUserDTO getByEmailForAuth(@RequestParam String email) {
        log.info("Request to get user by email for auth: {}", email);
        User user = userService.findByEmail(email);
        AuthUserDTO dto = UserMapper.toAuthDto(user);
        log.info("Returning auth DTO for user: {}", email);
        return dto;
    }

    @GetMapping("/by-email")
    public UserDTO getByEmail(@RequestParam String email) {
        log.info("Request to get user by email: {}", email);
        User user = userService.findByEmail(email);
        UserDTO dto = UserMapper.toDto(user);
        log.info("Returning user DTO for: {}", email);
        return dto;
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        log.info("Request to get user by id: {}", id);
        UserDTO dto = UserMapper.toDto(userService.findById(id));
        log.info("Returning user DTO for id: {}", id);
        return dto;
    }

    @GetMapping("/exists")
    public boolean emailExists(@RequestParam String email) {
        log.info("Checking if email exists: {}", email);
        boolean exists = userService.existsByEmail(email);
        log.info("Email {} exists: {}", email, exists);
        return exists;
    }

    /**
     * Mise à jour du mot de passe
     */
    @PutMapping("/{id}/update-password")
    public void updatePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.updatePassword(id, request.getNewPassword());
    }

    /**
     * Marquer que l'utilisateur a changé son mot de passe initial
     */
    @PutMapping("/{id}/mark-password-changed")
    public void markPasswordChanged(@PathVariable Long id) {
        log.info("Request to mark password changed for user id: {}", id);
        userService.setPasswordChanged(id);
        log.info("Password marked as changed for user id: {}", id);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody iusjc_planning.user_service.dto.CreateUserRequest request) {
        log.info("Request to create user: {}", request.getEmail());
        return UserMapper.toDto(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id,
            @RequestBody CreateUserRequest request) {
        log.info("Request to update user: {}", id);
        return UserMapper.toDto(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Request to delete user: {}", id);
        userService.deleteUser(id);
    }
}
