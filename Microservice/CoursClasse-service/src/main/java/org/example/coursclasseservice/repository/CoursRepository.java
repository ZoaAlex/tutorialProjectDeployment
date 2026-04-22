package org.example.coursclasseservice.repository;

import java.util.List;

import org.example.coursclasseservice.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    /** Retourne tous les cours avec ue chargés eagerly. */
    @Query("SELECT c FROM Cours c LEFT JOIN FETCH c.ue")
    List<Cours> findAllWithDetails();

    /** Retourne les cours dont le volume restant est > 0, avec ue chargés eagerly. */
    @Query("SELECT c FROM Cours c LEFT JOIN FETCH c.ue WHERE c.volumeHoraire > c.nbreheurefait")
    List<Cours> findCoursAPlanifier();

    /** Retourne un cours par id avec ue chargés eagerly. */
    @Query("SELECT c FROM Cours c LEFT JOIN FETCH c.ue WHERE c.id = :id")
    java.util.Optional<Cours> findByIdWithDetails(@org.springframework.data.repository.query.Param("id") Long id);
}
