package iusjc_planning.salles_service.mapper;

import iusjc_planning.salles_service.dto.CreateSalleRequest;
import iusjc_planning.salles_service.dto.SalleDTO;
import iusjc_planning.salles_service.model.Salle;
import iusjc_planning.salles_service.model.StatutSalle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre Salle et SalleDTO
 */
@Component
@Slf4j
public class SalleMapper {

    private final MaterielMapper materielMapper;

    public SalleMapper(MaterielMapper materielMapper) {
        this.materielMapper = materielMapper;
    }

    /**
     * Convertit une entité Salle en SalleDTO
     */
    public SalleDTO toDTO(Salle salle) {
        if (salle == null) {
            return null;
        }

        SalleDTO dto = new SalleDTO();
        dto.setId(salle.getId());
        dto.setCodeSalle(salle.getCodeSalle());
        dto.setNom(salle.getNom());
        dto.setCapacite(salle.getCapacite());
        dto.setTypeSalle(salle.getTypeSalle());
        dto.setStatut(salle.getStatut());
        dto.setDescription(salle.getDescription());
        dto.setEmplacement(salle.getEmplacement());
        dto.setEtage(salle.getEtage());
        dto.setBatiment(salle.getBatiment());
        dto.setSurface(salle.getSurface());
        dto.setAccessibleHandicap(salle.getAccessibleHandicap());
        dto.setClimatisee(salle.getClimatisee());
        dto.setWifiDisponible(salle.getWifiDisponible());
        dto.setDateCreation(salle.getDateCreation());
        dto.setDateModification(salle.getDateModification());
        dto.setCreePar(salle.getCreePar());
        dto.setModifiePar(salle.getModifiePar());

        // Conversion des matériels si présents
        if (salle.getMateriels() != null) {
            dto.setMateriels(salle.getMateriels().stream()
                    .map(materielMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        // Informations calculées
        dto.setDisponibleMaintenant(salle.isDisponible());
        if (salle.getReservations() != null) {
            dto.setNombreReservationsActives(
                    salle.getReservations().stream()
                            .filter(r -> r.isActive())
                            .count());
        }

        return dto;
    }

    /**
     * Convertit une liste d'entités Salle en liste de SalleDTO
     */
    public List<SalleDTO> toDTOList(List<Salle> salles) {
        if (salles == null) {
            return null;
        }
        return salles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un CreateSalleRequest en entité Salle
     */
    public Salle toEntity(CreateSalleRequest request) {
        if (request == null) {
            return null;
        }

        Salle salle = new Salle();
        salle.setCodeSalle(request.getCodeSalle());
        salle.setNom(request.getNom());
        salle.setCapacite(request.getCapacite());
        salle.setTypeSalle(request.getTypeSalle());
        salle.setStatut(StatutSalle.LIBRE); // Statut par défaut
        salle.setDescription(request.getDescription());
        salle.setEmplacement(request.getEmplacement());
        salle.setEtage(request.getEtage());
        salle.setBatiment(request.getBatiment());
        salle.setSurface(request.getSurface());
        salle.setAccessibleHandicap(request.getAccessibleHandicap());
        salle.setClimatisee(request.getClimatisee());
        salle.setWifiDisponible(request.getWifiDisponible());

        return salle;
    }

    /**
     * Met à jour une entité Salle existante avec les données d'un SalleDTO
     */
    public void updateEntity(Salle salle, SalleDTO dto) {
        if (salle == null || dto == null) {
            return;
        }

        salle.setCodeSalle(dto.getCodeSalle());
        salle.setNom(dto.getNom());
        salle.setCapacite(dto.getCapacite());
        salle.setTypeSalle(dto.getTypeSalle());
        salle.setStatut(dto.getStatut());
        salle.setDescription(dto.getDescription());
        salle.setEmplacement(dto.getEmplacement());
        salle.setEtage(dto.getEtage());
        salle.setBatiment(dto.getBatiment());
        salle.setSurface(dto.getSurface());
        salle.setAccessibleHandicap(dto.getAccessibleHandicap());
        salle.setClimatisee(dto.getClimatisee());
        salle.setWifiDisponible(dto.getWifiDisponible());
        salle.setModifiePar(dto.getModifiePar());
        salle.setDateModification(LocalDateTime.now());
    }
}