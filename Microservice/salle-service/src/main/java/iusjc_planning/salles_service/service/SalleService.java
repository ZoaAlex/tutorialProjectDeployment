package iusjc_planning.salles_service.service;

import iusjc_planning.salles_service.dto.*;
import iusjc_planning.salles_service.exception.ResourceNotFoundException;
import iusjc_planning.salles_service.exception.BusinessException;
import iusjc_planning.salles_service.mapper.SalleMapper;
import iusjc_planning.salles_service.model.Salle;
import iusjc_planning.salles_service.model.StatutSalle;
import iusjc_planning.salles_service.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion des salles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;
    private final SalleMapper salleMapper;

    /**
     * Créer une nouvelle salle
     */
    public SalleDTO creerSalle(CreateSalleRequest request, String utilisateur) {
        log.info("Création d'une nouvelle salle avec le code: {}", request.getCodeSalle());

        // Vérifier l'unicité du code salle
        if (salleRepository.existsByCodeSalle(request.getCodeSalle())) {
            throw new BusinessException("Une salle avec le code '" + request.getCodeSalle() + "' existe déjà");
        }

        Salle salle = salleMapper.toEntity(request);
        salle.setCreePar(utilisateur);
        salle.setDateCreation(LocalDateTime.now());

        Salle salleSauvegardee = salleRepository.save(salle);
        log.info("Salle créée avec succès - ID: {}", salleSauvegardee.getId());

        return salleMapper.toDTO(salleSauvegardee);
    }

    /**
     * Récupérer une salle par son ID
     */
    @Transactional(readOnly = true)
    public SalleDTO getSalleById(Long id) {
        log.debug("Récupération de la salle avec l'ID: {}", id);

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        return salleMapper.toDTO(salle);
    }

    /**
     * Récupérer une salle par son code
     */
    @Transactional(readOnly = true)
    public SalleDTO getSalleByCode(String codeSalle) {
        log.debug("Récupération de la salle avec le code: {}", codeSalle);

        Salle salle = salleRepository.findByCodeSalle(codeSalle)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec le code: " + codeSalle));

        return salleMapper.toDTO(salle);
    }

    /**
     * Récupérer toutes les salles
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> getAllSalles() {
        log.debug("Récupération de toutes les salles");

        List<Salle> salles = salleRepository.findAll();
        return salleMapper.toDTOList(salles);
    }

    /**
     * Rechercher des salles selon des critères
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> rechercherSalles(SalleSearchCriteria criteria) {
        log.debug("Recherche de salles avec critères: {}", criteria);

        List<Salle> salles;

        if (criteria.getDisponiblePourPeriode() != null && criteria.getDisponiblePourPeriode()
                && criteria.getDateDebut() != null && criteria.getDateFin() != null) {
            // Recherche avec critères de disponibilité
            salles = salleRepository.findSallesDisponiblesPourPeriode(
                    criteria.getDateDebut(), criteria.getDateFin());

        } else {
            // Recherche par critères multiples
            salles = salleRepository.findByMultipleCriteria(
                    criteria.getTypeSalle(),
                    criteria.getCapaciteMin(),
                    criteria.getCapaciteMax(),
                    criteria.getStatut(),
                    criteria.getAccessibleHandicap(),
                    criteria.getClimatisee(),
                    criteria.getWifiDisponible());
        }

        // Filtrage par terme de recherche si spécifié
        if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
            List<Salle> sallesParRecherche = salleRepository.findBySearchTerm(criteria.getSearchTerm());
            salles = salles.stream()
                    .filter(sallesParRecherche::contains)
                    .toList();
        }

        return salleMapper.toDTOList(salles);
    }

    /**
     * Récupérer les salles disponibles pour une période
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> getSallesDisponibles(LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.debug("Recherche de salles disponibles du {} au {}", dateDebut, dateFin);

        List<Salle> salles = salleRepository.findSallesDisponiblesPourPeriode(dateDebut, dateFin);

        return salleMapper.toDTOList(salles);
    }

    /**
     * Récupérer les salles avec une capacité suffisante et disponibles
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> getSallesAvecCapaciteDisponibles(Integer capaciteRequise,
            LocalDateTime dateDebut,
            LocalDateTime dateFin) {
        log.debug("Recherche de salles avec capacité >= {} disponibles du {} au {}",
                capaciteRequise, dateDebut, dateFin);

        List<Salle> salles = salleRepository.findSallesAvecCapaciteDisponibles(
                capaciteRequise, dateDebut, dateFin);

        return salleMapper.toDTOList(salles);
    }

    /**
     * Mettre à jour une salle
     */
    public SalleDTO mettreAJourSalle(Long id, SalleDTO salleDTO, String utilisateur) {
        log.info("Mise à jour de la salle avec l'ID: {}", id);

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        // Vérifier l'unicité du code salle si modifié
        if (!salle.getCodeSalle().equals(salleDTO.getCodeSalle()) &&
                salleRepository.existsByCodeSalle(salleDTO.getCodeSalle())) {
            throw new BusinessException("Une salle avec le code '" + salleDTO.getCodeSalle() + "' existe déjà");
        }

        salleMapper.updateEntity(salle, salleDTO);
        salle.setModifiePar(utilisateur);

        Salle salleModifiee = salleRepository.save(salle);
        log.info("Salle mise à jour avec succès - ID: {}", salleModifiee.getId());

        return salleMapper.toDTO(salleModifiee);
    }

    /**
     * Changer le statut d'une salle
     */
    public SalleDTO changerStatutSalle(Long id, StatutSalle nouveauStatut, String utilisateur) {
        log.info("Changement du statut de la salle {} vers: {}", id, nouveauStatut);

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        salle.setStatut(nouveauStatut);
        salle.setModifiePar(utilisateur);
        salle.setDateModification(LocalDateTime.now());

        Salle salleModifiee = salleRepository.save(salle);
        log.info("Statut de la salle changé avec succès");

        return salleMapper.toDTO(salleModifiee);
    }

    /**
     * Supprimer une salle
     */
    public void supprimerSalle(Long id, String utilisateur) {
        log.info("Suppression de la salle avec l'ID: {}", id);

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        // Vérifier s'il y a des réservations actives
        if (salle.getReservations() != null &&
                salle.getReservations().stream().anyMatch(r -> r.isActive())) {
            throw new BusinessException("Impossible de supprimer une salle avec des réservations actives");
        }

        salleRepository.delete(salle);
        log.info("Salle supprimée avec succès");
    }

    /**
     * Obtenir les statistiques des salles
     */
    @Transactional(readOnly = true)
    public Object getStatistiquesSalles() {
        log.debug("Récupération des statistiques des salles");

        return new Object() {
            public final List<Object[]> parStatut = salleRepository.countSallesParStatut();
            public final long totalSalles = salleRepository.count();
            public final long sallesDisponibles = salleRepository.countByStatut(StatutSalle.LIBRE);
            public final long sallesOccupees = salleRepository.countByStatut(StatutSalle.OCCUPEE);
            public final long sallesEnMaintenance = salleRepository.countByStatut(StatutSalle.MAINTENANCE);
        };
    }

    /**
     * Vérifier si une salle existe
     */
    @Transactional(readOnly = true)
    public boolean salleExiste(Long id) {
        return salleRepository.existsById(id);
    }

    /**
     * Vérifier si un code salle existe
     */
    @Transactional(readOnly = true)
    public boolean codeSalleExiste(String codeSalle) {
        return salleRepository.existsByCodeSalle(codeSalle);
    }
}