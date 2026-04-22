package org.example.specialeventservice.service;

import lombok.RequiredArgsConstructor;
import org.example.specialeventservice.clients.UserClient;
import org.example.specialeventservice.dto.DemandeEventDTO;
import org.example.specialeventservice.mapper.EventMapper;
import org.example.specialeventservice.model.DemandeEvent;
import org.example.specialeventservice.model.Enum.StatutDemande;
import org.example.specialeventservice.repository.DemandeEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandeEventService {

    private final DemandeEventRepository repository;
    private final EventMapper mapper;
    private final UserClient userClient;

    public DemandeEventDTO createDemande(DemandeEventDTO demandeDTO) {
        // Vérification de l'existence de l'enseignant via Feign Client
        Boolean exists = userClient.checkTeacherExists(demandeDTO.getEnseignantId());
        if (Boolean.FALSE.equals(exists)) {
            throw new RuntimeException("L'enseignant avec l'ID " + demandeDTO.getEnseignantId() + " n'existe pas.");
        }

        DemandeEvent entity = mapper.toDemandeEntity(demandeDTO);
        entity.setStatus(StatutDemande.EN_ATTENTE);
        return mapper.toDemandeDTO(repository.save(entity));
    }

    public DemandeEventDTO getDemandeById(Long id) {
        return repository.findById(id)
                .map(mapper::toDemandeDTO)
                .orElseThrow(() -> new RuntimeException("Demande not found with id: " + id));
    }

    public List<DemandeEventDTO> getAllDemandes() {
        return repository.findAll().stream()
                .map(mapper::toDemandeDTO)
                .collect(Collectors.toList());
    }

    public DemandeEventDTO updateStatut(Long id, StatutDemande status) {
        DemandeEvent entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande not found with id: " + id));
        entity.setStatus(status);
        return mapper.toDemandeDTO(repository.save(entity));
    }

    public void deleteDemande(Long id) {
        repository.deleteById(id);
    }
}
