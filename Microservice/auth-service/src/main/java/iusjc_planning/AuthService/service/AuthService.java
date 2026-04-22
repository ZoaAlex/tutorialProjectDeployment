package iusjc_planning.AuthService.service;

import feign.FeignException;
import iusjc_planning.AuthService.dto.*;
import iusjc_planning.AuthService.exception.InvalidCredentialsException;
import iusjc_planning.AuthService.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // Stockage temporaire des codes : Email -> {Code, Expiration}
    private final Map<String, ResetCodeInfo> resetCodes = new ConcurrentHashMap<>();

    private static class ResetCodeInfo {
        String code;
        LocalDateTime expiration;

        ResetCodeInfo(String code, LocalDateTime expiration) {
            this.code = code;
            this.expiration = expiration;
        }
    }

    /** LOGIN */
    public LoginResponse login(LoginRequest request) {

        // 1. VALIDATION LOCALE
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new InvalidCredentialsException("Email obligatoire.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidCredentialsException("Mot de passe obligatoire.");
        }

        try {
            // 2. RÉCUPÉRATION UTILISATEUR VIA FEIGN
            AuthUserDTO user = userClient.getByEmail(request.getEmail());

            log.info("[DEBUG] Login attempt for email: {}", request.getEmail());
            log.info("[DEBUG] Raw password from request: {}", request.getPassword());

            if (user == null) {
                log.warn("[DEBUG] User not found for email: {}", request.getEmail());
                throw new InvalidCredentialsException("Utilisateur inconnu.");
            }

            log.info("[DEBUG] Hashed password from DB: {}", user.getPassword());

            // 3. CHECK PASSWORD
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            log.info("[DEBUG] Password check outcome: {}", matches);

            if (user.getPassword() == null || !matches) {
                throw new InvalidCredentialsException("Mot de passe incorrect.");
            }

            // 4. CHECK STATUT DU COMPTE
            if (user.getStatut() != null && user.getStatut().equalsIgnoreCase("INACTIF")) {
                throw new InvalidCredentialsException("Compte désactivé. Contactez l'administrateur.");
            }

            // 5. CHECK PREMIÈRE CONNEXION
            if (user.isMustChangePassword()) {
                return new LoginResponse(null, true, user.getRole());
            }

            // 6. CHECK ROLES
            if (user.getRole() == null || user.getRole().isEmpty()) {
                throw new InvalidCredentialsException("Aucun rôle attribué à cet utilisateur.");
            }

            // 7. GÉNÉRATION DU TOKEN JWT
            String token = jwtService.generateToken(user.getEmail(), user.getRole());

            return new LoginResponse(token, false, user.getRole());

        } catch (FeignException.NotFound e) {
            throw new InvalidCredentialsException("Email ou mot de passe incorrect.");
        } catch (FeignException e) {
            throw new RuntimeException("Service utilisateur indisponible. Réessayez plus tard.");
        }
    }

    /** CHANGE PASSWORD */
    public void changePassword(String email, ChangePasswordRequest req) {

        // vérifier existence et mot de passe actuel
        AuthUserDTO user = userClient.getByEmail(email);

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Ancien mot de passe incorrect");
        }

        // Mise à jour du mot de passe via User-Service
        userClient.updatePassword(user.getId(), req);

        // Marquer première connexion comme terminée
        userClient.markPasswordChanged(user.getId());
    }

    /** FORGOT PASSWORD - STEP 1: REQUEST CODE */
    public void initiateForgotPassword(ForgotPasswordRequest request) {
        log.info("Requesting password reset for: {}", request.getEmail());

        // 1. Vérifier si l'utilisateur existe
        if (!userClient.existsByEmail(request.getEmail())) {
            // Pour des raisons de sécurité, on peut ne pas lever d'exception
            // mais ici on va simplifier pour le projet tutoré.
            throw new InvalidCredentialsException("Aucun compte associé à cet email.");
        }

        // 2. Générer un code à 6 chiffres
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 3. Stocker avec expiration (ex: 15 minutes)
        resetCodes.put(request.getEmail(), new ResetCodeInfo(code, LocalDateTime.now().plusMinutes(15)));

        // 4. Envoyer l'email
        String body = "Votre code de vérification pour la réinitialisation de votre mot de passe est : " + code +
                "\nCe code expirera dans 15 minutes.";
        emailService.sendEmail(request.getEmail(), "Récupération de mot de passe", body);
    }

    /** FORGOT PASSWORD - STEP 2: VERIFY CODE */
    public void verifyResetCode(String email, String code) {
        ResetCodeInfo info = resetCodes.get(email);

        if (info == null || !info.code.equals(code)) {
            throw new InvalidCredentialsException("Code invalide.");
        }

        if (info.expiration.isBefore(LocalDateTime.now())) {
            resetCodes.remove(email);
            throw new InvalidCredentialsException("Code expiré.");
        }

        log.info("Code verified successfully for {}", email);
    }

    /** FORGOT PASSWORD - STEP 3: RESET */
    public void resetPassword(ResetPasswordRequest req) {
        // Re-vérifier le code par sécurité
        verifyResetCode(req.getEmail(), req.getCode());

        AuthUserDTO user = userClient.getByEmail(req.getEmail());

        // On forge une ChangePasswordRequest factice pour réutiliser UserClient
        // car user-service attend l'ancien MDP pour valider d'habitude,
        // mais pour un "forgot password", l'admin/système force le mdp.
        // NOTE: Il se peut qu'il faille un endpoint spécifique dans User-Service pour
        // ça.
        // On va supposer que userClient.updatePassword peut fonctionner ou on crée un
        // DTO adapté.

        ChangePasswordRequest updateReq = new ChangePasswordRequest(null, req.getNewPassword());
        userClient.updatePassword(user.getId(), updateReq);

        // Nettoyer le code
        resetCodes.remove(req.getEmail());
        log.info("Password reset successful for {}", req.getEmail());
    }

    /** TEST CONNECTIVITY WITH USER-SERVICE */
    public String testConnectivity() {
        try {
            boolean exists = userClient.existsByEmail("admin@iusjc.com");
            return "Connexion réussie avec User-Service. (admin@iusjc.com existe : " + exists + ")";
        } catch (Exception e) {
            return "Échec de la connexion avec User-Service : " + e.getMessage();
        }
    }
}
