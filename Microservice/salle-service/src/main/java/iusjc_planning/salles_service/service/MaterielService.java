package iusjc_planning.salles_service.service;

import iusjc_planning.salles_service.dto.CreateMaterielRequest;
import iusjc_planning.salles_service.dto.MaterielDTO;
import iusjc_planning.salles_service.exception.ResourceNotFoundException;
import iusjc_planning.salles_service.mapper.MaterielMapper;
import iusjc_planning.salles_service.model.Materiel;
import iusjc_planning.salles_service.model.Salle;
import iusjc_planning.salles_service.model.TypeMateriel;
import iusjc_planning.salles_service.repository.MaterielRepository;
import iusjc_planning.salles_service.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion du matériel
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaterielService {

    private final MaterielRepository materielRepository;
    private final SalleRepository salleRepository;
    private final MaterielMapper materielMapper;

    /**
     * Créer un nouveau matériel
     */
    public MaterielDTO creerMateriel(CreateMaterielRequest request, String utilisateur) {
        log.info("Création d'un nouveau matériel: {} pour la salle: {}", request.getNom(), request.getSalleId());

        // Vérifier que la salle existe
        Salle salle = salleRepository.findById(request.getSalleId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + request.getSalleId()));

        Materiel materiel = materielMapper.toEntity(request);
        materiel.setSalle(salle);
        materiel.setCreePar(utilisateur);
        materiel.setDateCreation(LocalDateTime.now());

        Materiel materielSauvegarde = materielRepository.save(materiel);
        log.info("Matériel créé avec succès - ID: {}", materielSauvegarde.getId());

        return materielMapper.toDTO(materielSauvegarde);
    }

    /**
     * Récupérer un matériel par son ID
     */
    @Transactional(readOnly = true)
    public MaterielDTO getMaterielById(Long id) {
        log.debug("Récupération du matériel avec l'ID: {}", id);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'ID: " + id));

        return materielMapper.toDTO(materiel);
    }

    /**
     * Récupérer tout le matériel
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getAllMateriel() {
        log.debug("Récupération de tout le matériel");

        List<Materiel> materiels = materielRepository.findAll();
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel d'une salle
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielParSalle(Long salleId) {
        log.debug("Récupération du matériel pour la salle: {}", salleId);

        List<Materiel> materiels = materielRepository.findBySalleId(salleId);
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel fonctionnel d'une salle
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielFonctionnelParSalle(Long salleId) {
        log.debug("Récupération du matériel fonctionnel pour la salle: {}", salleId);

        List<Materiel> materiels = materielRepository.findBySalleIdAndEtat(salleId, "FONCTIONNEL");
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel par type
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielParType(TypeMateriel type) {
        log.debug("Récupération du matériel de type: {}", type);

        List<Materiel> materiels = materielRepository.findByType(type);
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel par état
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielParEtat(String etat) {
        log.debug("Récupération du matériel avec l'état: {}", etat);

        List<Materiel> materiels = materielRepository.findByEtat(etat);
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel nécessitant une maintenance
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielNecessitantMaintenance() {
        log.debug("Récupération du matériel nécessitant une maintenance");

        List<Materiel> materiels = materielRepository.findMaterielNecessitantMaintenance(LocalDateTime.now());
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel en panne
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielEnPanne() {
        log.debug("Récupération du matériel en panne");

        List<Materiel> materiels = materielRepository.findMaterielEnPanne();
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Récupérer le matériel en maintenance
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> getMaterielEnMaintenance() {
        log.debug("Récupération du matériel en maintenance");

        List<Materiel> materiels = materielRepository.findMaterielEnMaintenance();
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Mettre à jour un matériel
     */
    public MaterielDTO mettreAJourMateriel(Long id, MaterielDTO materielDTO, String utilisateur) {
        log.info("Mise à jour du matériel avec l'ID: {}", id);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'ID: " + id));

        materielMapper.updateEntity(materiel, materielDTO);
        materiel.setModifiePar(utilisateur);

        Materiel materielModifie = materielRepository.save(materiel);
        log.info("Matériel mis à jour avec succès - ID: {}", materielModifie.getId());

        return materielMapper.toDTO(materielModifie);
    }

    /**
     * Changer l'état d'un matériel
     */
    public MaterielDTO changerEtatMateriel(Long id, String nouvelEtat, String utilisateur) {
        log.info("Changement de l'état du matériel {} vers: {}", id, nouvelEtat);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'ID: " + id));

        String ancienEtat = materiel.getEtat();
        materiel.setEtat(nouvelEtat);
        materiel.setModifiePar(utilisateur);
        materiel.setDateModification(LocalDateTime.now());

        // Si passage en maintenance, enregistrer la date
        if ("EN_MAINTENANCE".equals(nouvelEtat)) {
            materiel.setDateDerniereMaintenance(LocalDateTime.now());
        }

        // Ajuster la quantité fonctionnelle selon l'état
        if ("EN_PANNE".equals(nouvelEtat) || "EN_MAINTENANCE".equals(nouvelEtat)) {
            if (materiel.getQuantiteFonctionnelle() > 0) {
                materiel.setQuantiteFonctionnelle(materiel.getQuantiteFonctionnelle() - 1);
            }
        } else if ("FONCTIONNEL".equals(nouvelEtat) &&
                ("EN_PANNE".equals(ancienEtat) || "EN_MAINTENANCE".equals(ancienEtat))) {
            if (materiel.getQuantiteFonctionnelle() < materiel.getQuantite()) {
                materiel.setQuantiteFonctionnelle(materiel.getQuantiteFonctionnelle() + 1);
            }
        }

        Materiel materielModifie = materielRepository.save(materiel);
        log.info("État du matériel changé avec succès");

        return materielMapper.toDTO(materielModifie);
    }

    /**
     * Programmer une maintenance
     */
    public MaterielDTO programmerMaintenance(Long id, LocalDateTime dateMaintenance, String utilisateur) {
        log.info("Programmation d'une maintenance pour le matériel {} le: {}", id, dateMaintenance);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'ID: " + id));

        materiel.setDateProchaineMaintenance(dateMaintenance);
        materiel.setModifiePar(utilisateur);
        materiel.setDateModification(LocalDateTime.now());

        Materiel materielModifie = materielRepository.save(materiel);
        log.info("Maintenance programmée avec succès");

        return materielMapper.toDTO(materielModifie);
    }

    /**
     * Déplacer un matériel vers une autre salle
     */
    public MaterielDTO deplacerMateriel(Long materielId, Long nouvelleSalleId, String utilisateur) {
        log.info("Déplacement du matériel {} vers la salle: {}", materielId, nouvelleSalleId);

        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'ID: " + materielId));

        Salle nouvelleSalle = salleRepository.findById(nouvelleSalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + nouvelleSalleId));

        materiel.setSalle(nouvelleSalle);
        materiel.setModifiePar(utilisateur);
        materiel.setDateModification(LocalDateTime.now());

        Materiel materielModifie = materielRepository.save(materiel);
        log.info("Matériel déplacé avec succès");

        return materielMapper.toDTO(materielModifie);
    }

    /**
     * Supprimer un matériel
     */
    public void supprimerMateriel(Long id, String utilisateur) {
        log.info("Suppression du matériel avec l'ID: {}", id);

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'ID: " + id));

        materielRepository.delete(materiel);
        log.info("Matériel supprimé avec succès");
    }

    /**
     * Rechercher du matériel par terme
     */
    @Transactional(readOnly = true)
    public List<MaterielDTO> rechercherMateriel(String searchTerm) {
        log.debug("Recherche de matériel avec le terme: {}", searchTerm);

        List<Materiel> materiels = materielRepository.findBySearchTerm(searchTerm);
        return materielMapper.toDTOList(materiels);
    }

    /**
     * Obtenir les statistiques du matériel
     */
    @Transactional(readOnly = true)
    public Object getStatistiquesMateriel() {
        log.debug("Récupération des statistiques du matériel");

        return new Object() {
            public final List<Object[]> parType = materielRepository.getStatistiquesParType();
            public final List<Object[]> parEtat = materielRepository.getStatistiquesParEtat();
            public final long totalMateriel = materielRepository.count();
            public final List<Materiel> materielEnPanne = materielRepository.findMaterielEnPanne();
            public final List<Materiel> materielMaintenanceRequise = materielRepository
                    .findMaterielNecessitantMaintenance(LocalDateTime.now());
        };
    }
}