package org.example.specialeventservice.service;

import lombok.RequiredArgsConstructor;
import org.example.specialeventservice.dto.SpecialEventDTO;
import org.example.specialeventservice.mapper.EventMapper;
import org.example.specialeventservice.model.DemandeEvent;
import org.example.specialeventservice.model.Enum.StatutDemande;
import org.example.specialeventservice.model.Enum.TypeSpecialEvent;
import org.example.specialeventservice.model.SpecialEvent;
import org.example.specialeventservice.repository.DemandeEventRepository;
import org.example.specialeventservice.repository.SpecialEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecialEventService {

    private final SpecialEventRepository specialEventRepository;
    private final DemandeEventRepository demandeEventRepository;
    private final EventMapper mapper;

    public SpecialEventDTO createEventFromDemande(Long demandeId, String type) {
        DemandeEvent demande = demandeEventRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande not found"));

        if (!demande.getStatus().equals(StatutDemande.VALIDE)) {
            throw new RuntimeException("Only validated demandes can be converted to special events");
        }

        SpecialEvent event = new SpecialEvent();
        event.setTitre(demande.getTitre());
        event.setDebutEvent(demande.getDebutEvent());
        event.setFinEvent(demande.getFinEvent());
        event.setTypeSpecialEvent(TypeSpecialEvent.valueOf(type.toUpperCase()));
        event.setDemandeEvent(demande);

        return mapper.toSpecialDTO(specialEventRepository.save(event));
    }

    public SpecialEventDTO updateEvent(Long id, SpecialEventDTO eventDTO) {
        SpecialEvent event = specialEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Special Event not found"));

        event.setTitre(eventDTO.getTitre());
        event.setDebutEvent(eventDTO.getDebutEvent());
        event.setFinEvent(eventDTO.getFinEvent());
        event.setTypeSpecialEvent(eventDTO.getTypeSpecialEvent());

        return mapper.toSpecialDTO(specialEventRepository.save(event));
    }

    public SpecialEventDTO getEventById(Long id) {
        return specialEventRepository.findById(id)
                .map(mapper::toSpecialDTO)
                .orElseThrow(() -> new RuntimeException("Special Event not found"));
    }

    public List<SpecialEventDTO> getAllEvents() {
        return specialEventRepository.findAll().stream()
                .map(mapper::toSpecialDTO)
                .collect(Collectors.toList());
    }

    public void deleteEvent(Long id) {
        specialEventRepository.deleteById(id);
    }
}
