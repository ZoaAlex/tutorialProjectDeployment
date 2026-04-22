package iusjc_planning.salles_service.mapper;

import iusjc_planning.salles_service.dto.CreateMaterielRequest;
import iusjc_planning.salles_service.dto.MaterielDTO;
import iusjc_planning.salles_service.model.Materiel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre Materiel et MaterielDTO
 */
@Component
public class MaterielMapper {

    /**
     * Convertit une entité Materiel en MaterielDTO
     */
    public MaterielDTO toDTO(Materiel materiel) {
        if (materiel == null) {
            return null;
        }

        MaterielDTO dto = new MaterielDTO();
        dto.setId(materiel.getId());
        dto.setNom(materiel.getNom());
        dto.setType(materiel.getType());
        dto.setDescription(materiel.getDescription());
        dto.setQuantite(materiel.getQuantite());
        dto.setQuantiteFonctionnelle(materiel.getQuantiteFonctionnelle());
        dto.setMarque(materiel.getMarque());
        dto.setModele(materiel.getModele());
        dto.setNumeroSerie(materiel.getNumeroSerie());
        dto.setDateAcquisition(materiel.getDateAcquisition());
        dto.setDateDerniereMaintenance(materiel.getDateDerniereMaintenance());
        dto.setDateProchaineMaintenance(materiel.getDateProchaineMaintenance());
        dto.setEtat(materiel.getEtat());
        dto.setObservations(materiel.getObservations());
        dto.setDateCreation(materiel.getDateCreation());
        dto.setDateModification(materiel.getDateModification());
        dto.setCreePar(materiel.getCreePar());
        dto.setModifiePar(materiel.getModifiePar());

        // Informations de la salle
        if (materiel.getSalle() != null) {
            dto.setSalleId(materiel.getSalle().getId());
            dto.setNomSalle(materiel.getSalle().getNom());
        }

        // Informations calculées
        dto.setMaintenanceRequise(materiel.isMaintenanceRequise());
        
        if (materiel.getDateDerniereMaintenance() != null) {
            dto.setJoursDepuisDerniereMaintenance(
                (int) ChronoUnit.DAYS.between(materiel.getDateDerniereMaintenance(), LocalDateTime.now())
            );
        }
        
        if (materiel.getDateProchaineMaintenance() != null) {
            dto.setJoursAvantProchaineMaintenance(
                (int) ChronoUnit.DAYS.between(LocalDateTime.now(), materiel.getDateProchaineMaintenance())
            );
        }

        return dto;
    }

    /**
     * Convertit une liste d'entités Materiel en liste de MaterielDTO
     */
    public List<MaterielDTO> toDTOList(List<Materiel> materiels) {
        if (materiels == null) {
            return null;
        }
        return materiels.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un CreateMaterielRequest en entité Materiel
     */
    public Materiel toEntity(CreateMaterielRequest request) {
        if (request == null) {
            return null;
        }

        Materiel materiel = new Materiel();
        materiel.setNom(request.getNom());
        materiel.setType(request.getType());
        materiel.setDescription(request.getDescription());
        materiel.setQuantite(request.getQuantite());
        materiel.setQuantiteFonctionnelle(request.getQuantiteFonctionnelle() != null ? 
                                         request.getQuantiteFonctionnelle() : request.getQuantite());
        materiel.setMarque(request.getMarque());
        materiel.setModele(request.getModele());
        materiel.setNumeroSerie(request.getNumeroSerie());
        materiel.setDateAcquisition(request.getDateAcquisition());
        materiel.setDateProchaineMaintenance(request.getDateProchaineMaintenance());
        materiel.setObservations(request.getObservations());
        materiel.setEtat("FONCTIONNEL"); // État par défaut

        return materiel;
    }

    /**
     * Met à jour une entité Materiel existante avec les données d'un MaterielDTO
     */
    public void updateEntity(Materiel materiel, MaterielDTO dto) {
        if (materiel == null || dto == null) {
            return;
        }

        materiel.setNom(dto.getNom());
        materiel.setType(dto.getType());
        materiel.setDescription(dto.getDescription());
        materiel.setQuantite(dto.getQuantite());
        materiel.setQuantiteFonctionnelle(dto.getQuantiteFonctionnelle());
        materiel.setMarque(dto.getMarque());
        materiel.setModele(dto.getModele());
        materiel.setNumeroSerie(dto.getNumeroSerie());
        materiel.setDateAcquisition(dto.getDateAcquisition());
        materiel.setDateDerniereMaintenance(dto.getDateDerniereMaintenance());
        materiel.setDateProchaineMaintenance(dto.getDateProchaineMaintenance());
        materiel.setEtat(dto.getEtat());
        materiel.setObservations(dto.getObservations());
        materiel.setModifiePar(dto.getModifiePar());
        materiel.setDateModification(LocalDateTime.now());
    }
}