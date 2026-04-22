package iusjc_planning.AuthService.AuthController;

import iusjc_planning.AuthService.dto.*;
import iusjc_planning.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /** LOGIN */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        log.info("Login successful for email: {}", request.getEmail());
        return response;
    }

    /** CHANGEMENT DE MOT DE PASSE */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String email,
            @RequestBody ChangePasswordRequest request) {
        log.info("Request to change password for email: {}", email);
        authService.changePassword(email, request);
        log.info("Password changed successfully for email: {}", email);
        return "Mot de passe modifié avec succès";
    }

    /** MOT DE PASSE OUBLIÉ - ÉTAPE 1: ENVOI DU CODE */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        authService.initiateForgotPassword(request);
        return "Code de réinitialisation envoyé par email";
    }

    /** MOT DE PASSE OUBLIÉ - ÉTAPE 2: VÉRIFICATION DU CODE */
    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam String email, @RequestParam String code) {
        log.info("Verifying reset code for email: {}", email);
        authService.verifyResetCode(email, code);
        return "Code valide";
    }

    /** MOT DE PASSE OUBLIÉ - ÉTAPE 3: RÉINITIALISATION */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("Resetting password for email: {}", request.getEmail());
        authService.resetPassword(request);
        return "Mot de passe réinitialisé avec succès";
    }

    /** TEST CONNECTION */
    @GetMapping("/test-connection")
    public String testConnection() {
        log.info("Testing connectivity...");
        String result = authService.testConnectivity();
        log.info("Connectivity test result: {}", result);
        return result;
    }
}
