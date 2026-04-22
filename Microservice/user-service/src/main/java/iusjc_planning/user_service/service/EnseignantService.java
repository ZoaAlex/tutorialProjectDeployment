package iusjc_planning.user_service.service;

import iusjc_planning.user_service.Exception.ResourceNotFoundException;
import iusjc_planning.user_service.dto.CreateEnseignantRequest;
import iusjc_planning.user_service.dto.EnseignantResponse;
import iusjc_planning.user_service.mapper.EnseignantMapper;
import iusjc_planning.user_service.model.Enseignant;
import iusjc_planning.user_service.repository.EnseignantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnseignantService {

    private static final Logger log = LoggerFactory.getLogger(EnseignantService.class);
    private final EnseignantRepository enseignantRepository;
    // Removed RoleService dependency
    private final PasswordEncoder passwordEncoder;

    public List<Enseignant> findAll() {
        return enseignantRepository.findAll();
    }

    public Enseignant findById(Long id) {
        return enseignantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));
    }

    public boolean existsById(Long id) {
        return enseignantRepository.existsById(id);
    }

    public EnseignantResponse createEnseignant(CreateEnseignantRequest request) {

        String rawPassword = (request.getPassword() != null && !request.getPassword().isBlank())
                ? request.getPassword()
                : "changeme@2026";

        log.info("Creating enseignant with email: {}", request.getEmail());

        Enseignant ens = EnseignantMapper.toEntity(request);
        ens.setRole("ROLE_ENSEIGNANT"); // Set role directly as string
        ens.setPassword(passwordEncoder.encode(rawPassword));

        Enseignant saved = enseignantRepository.save(ens);

        String message = String.format("L'enseignant %s %s a été créé avec succès. Mot de passe utilisé : %s",
                saved.getPrenom(), saved.getNom(), rawPassword);

        return EnseignantMapper.toResponse(saved, rawPassword, message);
    }
}
