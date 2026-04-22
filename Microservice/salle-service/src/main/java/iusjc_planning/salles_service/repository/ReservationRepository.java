package iusjc_planning.salles_service.repository;

import iusjc_planning.salles_service.model.Reservation;
import iusjc_planning.salles_service.model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des réservations
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

       // Recherche par salle
       List<Reservation> findBySalleId(Long salleId);

       // Recherche par utilisateur
       List<Reservation> findByUtilisateurId(Long utilisateurId);

       // Recherche par statut
       List<Reservation> findByStatut(StatutReservation statut);

       // Réservations par salle et statut
       List<Reservation> findBySalleIdAndStatut(Long salleId, StatutReservation statut);

       // Réservations par utilisateur et statut
       List<Reservation> findByUtilisateurIdAndStatut(Long utilisateurId, StatutReservation statut);

       // Réservations en attente
       @Query("SELECT r FROM Reservation r WHERE r.statut = 'EN_ATTENTE' ORDER BY r.dateCreation ASC")
       List<Reservation> findReservationsEnAttente();

       // Réservations validées pour une période
       @Query("SELECT r FROM Reservation r WHERE r.statut = 'VALIDEE' AND " +
                     "((r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))")
       List<Reservation> findReservationsValideesPourPeriode(
                     @Param("dateDebut") LocalDateTime dateDebut,
                     @Param("dateFin") LocalDateTime dateFin);

       // Réservations d'une salle pour une période
       @Query("SELECT r FROM Reservation r WHERE r.salle.id = :salleId AND " +
                     "r.statut IN ('VALIDEE', 'EN_ATTENTE') AND " +
                     "((r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))")
       List<Reservation> findReservationsSallePourPeriode(
                     @Param("salleId") Long salleId,
                     @Param("dateDebut") LocalDateTime dateDebut,
                     @Param("dateFin") LocalDateTime dateFin);

       // Vérifier conflit de réservation
       @Query("SELECT r FROM Reservation r WHERE r.salle.id = :salleId AND " +
                     "r.statut = 'VALIDEE' AND " +
                     "((r.dateDebut <= :dateDebut AND r.dateFin > :dateDebut) OR " +
                     "(r.dateDebut < :dateFin AND r.dateFin >= :dateFin) OR " +
                     "(r.dateDebut >= :dateDebut AND r.dateFin <= :dateFin))")
       List<Reservation> findConflitsReservation(
                     @Param("salleId") Long salleId,
                     @Param("dateDebut") LocalDateTime dateDebut,
                     @Param("dateFin") LocalDateTime dateFin);

       // Réservations actives (en cours)
       @Query("SELECT r FROM Reservation r WHERE r.statut = 'VALIDEE' AND " +
                     ":maintenant BETWEEN r.dateDebut AND r.dateFin")
       List<Reservation> findReservationsActives(@Param("maintenant") LocalDateTime maintenant);

       // Réservations à venir
       @Query("SELECT r FROM Reservation r WHERE r.statut = 'VALIDEE' AND " +
                     "r.dateDebut > :maintenant ORDER BY r.dateDebut ASC")
       List<Reservation> findReservationsAVenir(@Param("maintenant") LocalDateTime maintenant);

       // Réservations passées
       @Query("SELECT r FROM Reservation r WHERE r.dateFin < :maintenant ORDER BY r.dateFin DESC")
       List<Reservation> findReservationsPassees(@Param("maintenant") LocalDateTime maintenant);

       // Réservations récurrentes
       List<Reservation> findByRecurrente(Boolean recurrente);

       // Réservations par priorité
       List<Reservation> findByPrioriteOrderByDateCreationAsc(Integer priorite);

       // Statistiques par statut
       @Query("SELECT r.statut, COUNT(r) FROM Reservation r GROUP BY r.statut")
       List<Object[]> getStatistiquesParStatut();

       // Statistiques par mois
       @Query("SELECT YEAR(r.dateCreation), MONTH(r.dateCreation), COUNT(r) FROM Reservation r " +
                     "GROUP BY YEAR(r.dateCreation), MONTH(r.dateCreation) ORDER BY YEAR(r.dateCreation), MONTH(r.dateCreation)")
       List<Object[]> getStatistiquesParMois();

       // Réservations longues (plus de X heures)
       @Query("SELECT r FROM Reservation r WHERE " +
                     "TIMESTAMPDIFF(HOUR, r.dateDebut, r.dateFin) > :heuresMin")
       List<Reservation> findReservationsLongues(@Param("heuresMin") int heuresMin);

       // Réservations avec beaucoup de participants
       List<Reservation> findByNombreParticipantsGreaterThan(Integer nombreMin);

       // Recherche textuelle dans motif et description
       @Query("SELECT r FROM Reservation r WHERE " +
                     "LOWER(r.motif) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(r.commentaires) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       List<Reservation> findBySearchTerm(@Param("searchTerm") String searchTerm);

       // Réservations nécessitant validation (anciennes en attente)
       @Query("SELECT r FROM Reservation r WHERE r.statut = 'EN_ATTENTE' AND " +
                     "r.dateCreation < :dateLimit ORDER BY r.dateCreation ASC")
       List<Reservation> findReservationsNecessitantValidation(@Param("dateLimit") LocalDateTime dateLimit);

       // Compter réservations par utilisateur
       long countByUtilisateurId(Long utilisateurId);

       // Compter réservations par salle
       long countBySalleId(Long salleId);

       // Compter réservations par statut
       long countByStatut(StatutReservation statut);

       // Réservations par critères multiples
       @Query("SELECT r FROM Reservation r WHERE " +
                     "(:salleId IS NULL OR r.salle.id = :salleId) AND " +
                     "(:utilisateurId IS NULL OR r.utilisateurId = :utilisateurId) AND " +
                     "(:statut IS NULL OR r.statut = :statut) AND " +
                     "(:dateDebut IS NULL OR r.dateDebut >= :dateDebut) AND " +
                     "(:dateFin IS NULL OR r.dateFin <= :dateFin)")
       List<Reservation> findByMultipleCriteria(
                     @Param("salleId") Long salleId,
                     @Param("utilisateurId") Long utilisateurId,
                     @Param("statut") StatutReservation statut,
                     @Param("dateDebut") LocalDateTime dateDebut,
                     @Param("dateFin") LocalDateTime dateFin);
}