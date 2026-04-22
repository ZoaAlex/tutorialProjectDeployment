package iusjc_planning.salles_service.repository;

import iusjc_planning.salles_service.model.Salle;
import iusjc_planning.salles_service.model.StatutSalle;
import iusjc_planning.salles_service.model.TypeSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des salles
 */
@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {

       // Recherche par code salle
       Optional<Salle> findByCodeSalle(String codeSalle);

       // Recherche par statut
       List<Salle> findByStatut(StatutSalle statut);

       // Recherche par type de salle
       List<Salle> findByTypeSalle(TypeSalle typeSalle);

       // Recherche par capacité minimale
       List<Salle> findByCapaciteGreaterThanEqual(Integer capaciteMin);

       // Recherche par critères multiples
       @Query("SELECT s FROM Salle s WHERE " +
                     "(:typeSalle IS NULL OR s.typeSalle = :typeSalle) AND " +
                     "(:capaciteMin IS NULL OR s.capacite >= :capaciteMin) AND " +
                     "(:capaciteMax IS NULL OR s.capacite <= :capaciteMax) AND " +
                     "(:statut IS NULL OR s.statut = :statut) AND " +
                     "(:accessibleHandicap IS NULL OR s.accessibleHandicap = :accessibleHandicap) AND " +
                     "(:climatisee IS NULL OR s.climatisee = :climatisee) AND " +
                     "(:wifiDisponible IS NULL OR s.wifiDisponible = :wifiDisponible)")
       List<Salle> findByMultipleCriteria(
                     @Param("typeSalle") TypeSalle typeSalle,
                     @Param("capaciteMin") Integer capaciteMin,
                     @Param("capaciteMax") Integer capaciteMax,
                     @Param("statut") StatutSalle statut,
                     @Param("accessibleHandicap") Boolean accessibleHandicap,
                     @Param("climatisee") Boolean climatisee,
                     @Param("wifiDisponible") Boolean wifiDisponible);

       // Salles disponibles pour une période donnée
       @Query("SELECT s FROM Salle s WHERE s.statut = 'LIBRE' AND s.id NOT IN " +
                     "(SELECT r.salle.id FROM Reservation r WHERE " +
                     "r.statut = 'VALIDEE' AND " +
                     "((r.dateDebut <= :dateDebut AND r.dateFin > :dateDebut) OR " +
                     "(r.dateDebut < :dateFin AND r.dateFin >= :dateFin) OR " +
                     "(r.dateDebut >= :dateDebut AND r.dateFin <= :dateFin)))")
       List<Salle> findSallesDisponiblesPourPeriode(
                     @Param("dateDebut") LocalDateTime dateDebut,
                     @Param("dateFin") LocalDateTime dateFin);

       // Salles avec capacité suffisante et disponibles
       @Query("SELECT s FROM Salle s WHERE s.capacite >= :capaciteRequise AND s.statut = 'LIBRE' AND s.id NOT IN " +
                     "(SELECT r.salle.id FROM Reservation r WHERE " +
                     "r.statut = 'VALIDEE' AND " +
                     "((r.dateDebut <= :dateDebut AND r.dateFin > :dateDebut) OR " +
                     "(r.dateDebut < :dateFin AND r.dateFin >= :dateFin) OR " +
                     "(r.dateDebut >= :dateDebut AND r.dateFin <= :dateFin)))")
       List<Salle> findSallesAvecCapaciteDisponibles(
                     @Param("capaciteRequise") Integer capaciteRequise,
                     @Param("dateDebut") LocalDateTime dateDebut,
                     @Param("dateFin") LocalDateTime dateFin);

       // Recherche textuelle dans nom et description
       @Query("SELECT s FROM Salle s WHERE " +
                     "LOWER(s.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(s.codeSalle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       List<Salle> findBySearchTerm(@Param("searchTerm") String searchTerm);

       // Statistiques par statut
       @Query("SELECT s.statut, COUNT(s) FROM Salle s GROUP BY s.statut")
       List<Object[]> countSallesParStatut();

       // Salles nécessitant une maintenance (avec matériel en maintenance)
       @Query("SELECT DISTINCT s FROM Salle s JOIN s.materiels m WHERE m.etat = 'EN_MAINTENANCE'")
       List<Salle> findSallesAvecMaterielEnMaintenance();

       // Salles avec matériel spécifique
       @Query("SELECT DISTINCT s FROM Salle s JOIN s.materiels m WHERE m.type = :typeMateriel AND m.etat = 'FONCTIONNEL'")
       List<Salle> findSallesAvecMaterielType(@Param("typeMateriel") String typeMateriel);

       // Vérifier si une salle existe par code
       boolean existsByCodeSalle(String codeSalle);

       // Compter les salles par statut
       long countByStatut(StatutSalle statut);

       // Salles par bâtiment
       List<Salle> findByBatiment(String batiment);

       // Salles par étage
       List<Salle> findByEtage(Integer etage);
}