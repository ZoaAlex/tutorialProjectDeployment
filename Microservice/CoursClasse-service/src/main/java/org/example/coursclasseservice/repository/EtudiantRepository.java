package org.example.coursclasseservice.repository;

import org.example.coursclasseservice.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    boolean existsByMatricule(String matricule);
    Optional<Etudiant> findByMatricule(String matricule);
}
