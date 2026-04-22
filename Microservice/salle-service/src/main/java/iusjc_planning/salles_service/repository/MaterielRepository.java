package iusjc_planning.salles_service.repository;

import iusjc_planning.salles_service.model.Materiel;
import iusjc_planning.salles_service.model.TypeMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion du matériel
 */
@Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long> {

        // Recherche par salle
        List<Materiel> findBySalleId(Long salleId);

        // Recherche par type de matériel
        List<Materiel> findByType(TypeMateriel type);

        // Recherche par état
        List<Materiel> findByEtat(String etat);

        // Matériel fonctionnel d'une salle
        List<Materiel> findBySalleIdAndEtat(Long salleId, String etat);

        // Matériel par type et état
        List<Materiel> findByTypeAndEtat(TypeMateriel type, String etat);

        // Matériel nécessitant une maintenance
        @Query("SELECT m FROM Materiel m WHERE m.dateProchaineMaintenance <= :dateActuelle")
        List<Materiel> findMaterielNecessitantMaintenance(@Param("dateActuelle") LocalDateTime dateActuelle);

        // Matériel en panne
        @Query("SELECT m FROM Materiel m WHERE m.etat = 'EN_PANNE'")
        List<Materiel> findMaterielEnPanne();

        // Matériel en maintenance
        @Query("SELECT m FROM Materiel m WHERE m.etat = 'EN_MAINTENANCE'")
        List<Materiel> findMaterielEnMaintenance();

        // Statistiques par type de matériel
        @Query("SELECT m.type, COUNT(m), SUM(m.quantite) FROM Materiel m GROUP BY m.type")
        List<Object[]> getStatistiquesParType();

        // Statistiques par état
        @Query("SELECT m.etat, COUNT(m), SUM(m.quantite) FROM Materiel m GROUP BY m.etat")
        List<Object[]> getStatistiquesParEtat();

        // Recherche par marque
        List<Materiel> findByMarque(String marque);

        // Recherche par modèle
        List<Materiel> findByModele(String modele);

        // Recherche par numéro de série
        List<Materiel> findByNumeroSerie(String numeroSerie);

        // Matériel acquis dans une période
        @Query("SELECT m FROM Materiel m WHERE m.dateAcquisition BETWEEN :dateDebut AND :dateFin")
        List<Materiel> findMaterielAcquisDansPeriode(
                        @Param("dateDebut") LocalDateTime dateDebut,
                        @Param("dateFin") LocalDateTime dateFin);

        // Matériel avec maintenance dans une période
        @Query("SELECT m FROM Materiel m WHERE m.dateProchaineMaintenance BETWEEN :dateDebut AND :dateFin")
        List<Materiel> findMaterielMaintenanceDansPeriode(
                        @Param("dateDebut") LocalDateTime dateDebut,
                        @Param("dateFin") LocalDateTime dateFin);

        // Recherche textuelle
        @Query("SELECT m FROM Materiel m WHERE " +
                        "LOWER(m.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(m.marque) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(m.modele) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        List<Materiel> findBySearchTerm(@Param("searchTerm") String searchTerm);

        // Compter matériel par salle
        long countBySalleId(Long salleId);

        // Compter matériel fonctionnel par salle
        long countBySalleIdAndEtat(Long salleId, String etat);

        // Matériel avec quantité insuffisante (quantité fonctionnelle < quantité
        // totale)
        @Query("SELECT m FROM Materiel m WHERE m.quantiteFonctionnelle < m.quantite")
        List<Materiel> findMaterielAvecQuantiteInsuffisante();

        // Matériel par critères multiples
        @Query("SELECT m FROM Materiel m WHERE " +
                        "(:salleId IS NULL OR m.salle.id = :salleId) AND " +
                        "(:type IS NULL OR m.type = :type) AND " +
                        "(:etat IS NULL OR m.etat = :etat) AND " +
                        "(:marque IS NULL OR LOWER(m.marque) LIKE LOWER(CONCAT('%', :marque, '%')))")
        List<Materiel> findByMultipleCriteria(
                        @Param("salleId") Long salleId,
                        @Param("type") TypeMateriel type,
                        @Param("etat") String etat,
                        @Param("marque") String marque);
}