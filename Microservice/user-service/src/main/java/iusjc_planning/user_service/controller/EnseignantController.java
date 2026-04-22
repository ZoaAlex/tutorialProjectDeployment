package iusjc_planning.user_service.controller;

import iusjc_planning.user_service.dto.CreateEnseignantRequest;
import iusjc_planning.user_service.dto.EnseignantDTO;
import iusjc_planning.user_service.dto.EnseignantResponse;
import iusjc_planning.user_service.mapper.EnseignantMapper;
import iusjc_planning.user_service.service.EnseignantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
@RequiredArgsConstructor
@Slf4j
public class EnseignantController {

    private final EnseignantService enseignantService;

    @PostMapping
    public EnseignantResponse create(@RequestBody CreateEnseignantRequest req) {
        log.info("Request to create enseignant: {} {}", req.getPrenom(), req.getNom());
        EnseignantResponse response = enseignantService.createEnseignant(req);
        log.info("Enseignant created successfully with email: {}", response.getEmail());
        return response;
    }

    @GetMapping
    public List<EnseignantDTO> getAll() {
        return enseignantService.findAll().stream()
                .map(EnseignantMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public EnseignantDTO getOne(@PathVariable Long id) {
        return EnseignantMapper.toDto(enseignantService.findById(id));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existByid(@PathVariable long id) {
        return ResponseEntity.ok(enseignantService.existsById(id));
    }

}
