package iusjc_planning.user_service.service;

import iusjc_planning.user_service.Exception.ResourceNotFoundException;
import iusjc_planning.user_service.model.User;
import iusjc_planning.user_service.model.Enseignant;
import iusjc_planning.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
    }

    /** Récupérer tous les utilisateurs (Admin + Enseignants) */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Rechercher un user par ID */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void setPasswordChanged(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ----------------------------------------------------------------
    // CRUD IMPLEMENTATION
    // ----------------------------------------------------------------

    public User createUser(iusjc_planning.user_service.dto.CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        User user;
        // Check if role contains ENSEIGNANT to instantiate Enseignant class
        boolean isEnseignant = request.getRole() != null && request.getRole().toUpperCase().contains("ENSEIGNANT");

        if (isEnseignant) {
            Enseignant enseignant = new Enseignant();
            enseignant.setSpecialite(request.getSpecialite());
            enseignant.setGrade(request.getGrade());
            user = enseignant;
        } else {
            user = new User();
        }

        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatut(iusjc_planning.user_service.model.StatutUser.ACTIF);

        // Handle Role (Single String)
        // Ensure format is ROLE_...
        String roleName = request.getRole() != null ? request.getRole().toUpperCase() : "ROLE_USER";
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        user.setRole(roleName);

        return userRepository.save(user);
    }

    public User updateUser(Long id, iusjc_planning.user_service.dto.CreateUserRequest request) {
        User user = findById(id);

        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());

        // Only update password if provided and not empty
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (user instanceof Enseignant && request.getRole() != null
                && request.getRole().toUpperCase().contains("ENSEIGNANT")) {
            ((Enseignant) user).setSpecialite(request.getSpecialite());
            ((Enseignant) user).setGrade(request.getGrade());
        }

        // Update Role if provided
        if (request.getRole() != null) {
            String roleName = request.getRole().toUpperCase();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            user.setRole(roleName);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }
        userRepository.deleteById(id);
    }
}
