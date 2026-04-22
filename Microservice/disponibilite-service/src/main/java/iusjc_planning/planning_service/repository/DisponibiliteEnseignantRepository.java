package iusjc_planning.planning_service.repository;

import iusjc_planning.planning_service.model.DisponibiliteEnseignant;
import iusjc_planning.planning_service.model.JourSemaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DisponibiliteEnseignantRepository extends JpaRepository<DisponibiliteEnseignant, Long> {

    List<DisponibiliteEnseignant> findByEnseignantId(Long enseignantId);

    List<DisponibiliteEnseignant> findByEnseignantIdAndJour(Long enseignantId, JourSemaine jour);

    List<DisponibiliteEnseignant> findByEnseignantIdAndEstDisponible(Long enseignantId, Boolean estDisponible);


    @Query("SELECT d FROM DisponibiliteEnseignant d WHERE d.enseignantId = :enseignantId " +
           "AND d.jour = :jour AND d.estDisponible = true " +
           "AND d.heureDebut <= :heure AND d.heureFin > :heure")
    List<DisponibiliteEnseignant> findDisponibilitesAHeure(@Param("enseignantId") Long enseignantId,
                                                           @Param("jour") JourSemaine jour,
                                                           @Param("heure") LocalTime heure);

    @Query("SELECT d FROM DisponibiliteEnseignant d WHERE d.enseignantId IN :enseignantIds " +
           "AND d.jour = :jour AND d.estDisponible = true " +
           "AND d.heureDebut <= :heureDebut AND d.heureFin >= :heureFin")
    List<DisponibiliteEnseignant> findEnseignantsDisponibles(@Param("enseignantIds") List<Long> enseignantIds,
                                                             @Param("jour") JourSemaine jour,
                                                             @Param("heureDebut") LocalTime heureDebut,
                                                             @Param("heureFin") LocalTime heureFin);

    void deleteByEnseignantId(Long enseignantId);
}