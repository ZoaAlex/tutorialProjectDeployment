package iusjc_planning.user_service.config;

import iusjc_planning.user_service.model.*;
import iusjc_planning.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // -----------------------------
            // 1. AJOUT ADMIN PAR DEFAUT
            // -----------------------------
            if (!userRepository.existsByEmail("admin@iusjc.com")) {

                User admin = new User();
                admin.setNom("Super");
                admin.setPrenom("Admin");
                admin.setEmail("admin@iusjc.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setMustChangePassword(false);
                admin.setStatut(StatutUser.ACTIF);
                admin.setRole("ROLE_ADMIN");

                userRepository.save(admin);
                System.out.println("ADMIN créé !");
            }

            // -----------------------------
            // 2. AJOUT ENSEIGNANT PAR DEFAUT
            // -----------------------------
            if (!userRepository.existsByEmail("enseignant@iusjc.com")) {

                User ens = new User();
                ens.setNom("Doe");
                ens.setPrenom("John");
                ens.setEmail("enseignant@iusjc.com");
                ens.setPassword(passwordEncoder.encode("enseignant123"));
                ens.setMustChangePassword(true); // Doit changer à la première connexion
                ens.setStatut(StatutUser.ACTIF);
                ens.setRole("ROLE_ENSEIGNANT");

                userRepository.save(ens);
                System.out.println("ENSEIGNANT créé !");
            }

            System.out.println("Initialisation des données terminée.");
        };
    }
}
