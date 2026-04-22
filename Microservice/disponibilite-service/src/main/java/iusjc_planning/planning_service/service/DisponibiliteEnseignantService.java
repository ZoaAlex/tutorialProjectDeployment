package iusjc_planning.planning_service.service;

import iusjc_planning.planning_service.dto.CreateDisponibiliteRequest;
import iusjc_planning.planning_service.dto.DisponibiliteEnseignantDTO;
import iusjc_planning.planning_service.exception.BusinessException;
import iusjc_planning.planning_service.exception.ResourceNotFoundException;
import iusjc_planning.planning_service.mapper.DisponibiliteEnseignantMapper;
import iusjc_planning.planning_service.model.DisponibiliteEnseignant;
import iusjc_planning.planning_service.feign.UserClient;
import iusjc_planning.planning_service.model.JourSemaine;
import iusjc_planning.planning_service.repository.DisponibiliteEnseignantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DisponibiliteEnseignantService {

    private final DisponibiliteEnseignantRepository disponibiliteRepository;
    private final UserClient userClient;
    private final DisponibiliteEnseignantMapper disponibiliteMapper;

    public DisponibiliteEnseignantDTO createDisponibilite(CreateDisponibiliteRequest request) {
        log.info("Création d'une nouvelle disponibilité pour l'enseignant: {}", request.getEnseignantId());

        // Validation des heures
        if (request.getHeureDebut().isAfter(request.getHeureFin())) {
            throw new BusinessException("L'heure de début doit être antérieure à l'heure de fin");
        }

        // Récupérer l'enseignant (vérification via Feign)
        try {
            userClient.getEnseignantById(request.getEnseignantId());
        } catch (Exception e) {
            log.warn("L'enseignant {} n'a pas été trouvé dans user-service lors de la création, mais on continue.",
                    request.getEnseignantId());
        }

        DisponibiliteEnseignant disponibilite = disponibiliteMapper.toEntity(request, request.getEnseignantId());
        DisponibiliteEnseignant savedDisponibilite = disponibiliteRepository.save(disponibilite);

        log.info("Disponibilité créée avec succès avec l'ID: {}", savedDisponibilite.getId());
        return disponibiliteMapper.toDTO(savedDisponibilite);
    }

    @Transactional(readOnly = true)
    public DisponibiliteEnseignantDTO getDisponibiliteById(Long id) {
        log.info("Récupération de la disponibilité avec l'ID: {}", id);

        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID: " + id));

        return disponibiliteMapper.toDTO(disponibilite);
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteEnseignantDTO> getDisponibilitesByEnseignant(Long enseignantId) {
        log.info("Récupération des disponibilités pour l'enseignant: {}", enseignantId);

        // Vérifier que l'enseignant existe (optionnel pour la lecture)
        try {
            userClient.getEnseignantById(enseignantId);
        } catch (Exception e) {
            log.warn(
                    "L'enseignant {} n'a pas été trouvé dans user-service, mais on continue pour renvoyer la liste vide si besoin.",
                    enseignantId);
        }

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository.findByEnseignantId(enseignantId);
        return disponibiliteMapper.toDTOList(disponibilites);
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteEnseignantDTO> getDisponibilitesByEnseignantAndJour(Long enseignantId, JourSemaine jour) {
        log.info("Récupération des disponibilités pour l'enseignant {} le {}", enseignantId, jour);

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository
                .findByEnseignantIdAndJour(enseignantId, jour);
        return disponibiliteMapper.toDTOList(disponibilites);
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteEnseignantDTO> getDisponibilitesActives(Long enseignantId) {
        log.info("Récupération des disponibilités actives pour l'enseignant: {}", enseignantId);

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository
                .findByEnseignantIdAndEstDisponible(enseignantId, true);
        return disponibiliteMapper.toDTOList(disponibilites);
    }

    @Transactional(readOnly = true)
    public boolean isEnseignantDisponible(Long enseignantId, JourSemaine jour, LocalTime heure) {
        log.info("Vérification de la disponibilité de l'enseignant {} le {} à {}", enseignantId, jour, heure);

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository
                .findDisponibilitesAHeure(enseignantId, jour, heure);

        return !disponibilites.isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean isEnseignantDisponiblePourCreneau(Long enseignantId, JourSemaine jour,
            LocalTime heureDebut, LocalTime heureFin) {
        log.info("Vérification de la disponibilité de l'enseignant {} le {} de {} à {}",
                enseignantId, jour, heureDebut, heureFin);

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository
                .findEnseignantsDisponibles(List.of(enseignantId), jour, heureDebut, heureFin);

        return !disponibilites.isEmpty();
    }

    public DisponibiliteEnseignantDTO updateDisponibilite(Long id, CreateDisponibiliteRequest request) {
        log.info("Mise à jour de la disponibilité avec l'ID: {}", id);

        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID: " + id));

        // Validation des heures
        if (request.getHeureDebut().isAfter(request.getHeureFin())) {
            throw new BusinessException("L'heure de début doit être antérieure à l'heure de fin");
        }

        // Mettre à jour les champs
        disponibilite.setJour(request.getJour());
        disponibilite.setHeureDebut(request.getHeureDebut());
        disponibilite.setHeureFin(request.getHeureFin());
        disponibilite.setType(request.getType());
        disponibilite.setCommentaire(request.getCommentaire());
        disponibilite.setEstDisponible(request.getEstDisponible());

        DisponibiliteEnseignant updatedDisponibilite = disponibiliteRepository.save(disponibilite);

        log.info("Disponibilité mise à jour avec succès");
        return disponibiliteMapper.toDTO(updatedDisponibilite);
    }

    public void deleteDisponibilite(Long id) {
        log.info("Suppression de la disponibilité avec l'ID: {}", id);

        if (!disponibiliteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disponibilité non trouvée avec l'ID: " + id);
        }

        disponibiliteRepository.deleteById(id);
        log.info("Disponibilité supprimée avec succès");
    }

    public void deleteAllDisponibilitesByEnseignant(Long enseignantId) {
        log.info("Suppression de toutes les disponibilités pour l'enseignant: {}", enseignantId);

        try {
            userClient.getEnseignantById(enseignantId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Enseignant non trouvé avec l'ID: " + enseignantId);
        }

        disponibiliteRepository.deleteByEnseignantId(enseignantId);
        log.info("Toutes les disponibilités supprimées pour l'enseignant: {}", enseignantId);
    }

    public DisponibiliteEnseignantDTO toggleDisponibilite(Long id) {
        log.info("Basculement du statut de disponibilité pour l'ID: {}", id);

        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID: " + id));

        disponibilite.setEstDisponible(!disponibilite.getEstDisponible());
        DisponibiliteEnseignant updatedDisponibilite = disponibiliteRepository.save(disponibilite);

        log.info("Statut de disponibilité basculé avec succès");
        return disponibiliteMapper.toDTO(updatedDisponibilite);
    }
}