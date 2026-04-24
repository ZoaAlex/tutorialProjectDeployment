package iusjc_planning.user_service.service;

import iusjc_planning.user_service.model.Enseignant;
import iusjc_planning.user_service.model.StatutUser;
import iusjc_planning.user_service.model.User;
import iusjc_planning.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Service d'import d'utilisateurs depuis un fichier Excel.
 *
 * Format attendu (ligne 1 = en-tête ignorée) :
 * | nom | prenom | email | password | role | specialite | grade |
 *
 * - role : ENSEIGNANT ou ADMIN (la valeur ROLE_ est ajoutée automatiquement)
 * - specialite et grade : optionnels, utilisés uniquement si role = ENSEIGNANT
 * - password : en clair dans le fichier, hashé avant sauvegarde
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserImportService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> importerUtilisateurs(MultipartFile file) {
        List<User> toSave   = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header

                try {
                    User user = mapRowToUser(row);
                    toSave.add(user);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                    log.warn("Erreur ligne {} : {}", row.getRowNum() + 1, e.getMessage());
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            userRepository.saveAll(toSave);
            log.info("{} utilisateurs importés", toSave.size());
            return ResponseEntity.ok(toSave.size() + " utilisateur(s) importé(s) avec succès");

        } catch (Exception e) {
            log.error("Erreur lecture fichier Excel : {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Erreur lecture fichier : " + e.getMessage());
        }
    }

    private User mapRowToUser(Row row) {
        String nom       = getCellString(row, 0);
        String prenom    = getCellString(row, 1);
        String email     = getCellString(row, 2);
        String password  = getCellString(row, 3);
        String roleRaw   = getCellString(row, 4);
        String specialite = getCellString(row, 5);
        String grade     = getCellString(row, 6);

        if (nom.isBlank() || prenom.isBlank() || email.isBlank() || password.isBlank() || roleRaw.isBlank()) {
            throw new IllegalArgumentException("Champs obligatoires manquants (nom, prenom, email, password, role)");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email déjà utilisé : " + email);
        }

        String role = roleRaw.toUpperCase();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        User user;
        if (role.contains("ENSEIGNANT")) {
            Enseignant enseignant = new Enseignant();
            enseignant.setSpecialite(specialite.isBlank() ? null : specialite);
            enseignant.setGrade(grade.isBlank() ? null : grade);
            user = enseignant;
        } else {
            user = new User();
        }

        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatut(StatutUser.ACTIF);
        user.setMustChangePassword(true);

        return user;
    }

    private String getCellString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
    }
}
