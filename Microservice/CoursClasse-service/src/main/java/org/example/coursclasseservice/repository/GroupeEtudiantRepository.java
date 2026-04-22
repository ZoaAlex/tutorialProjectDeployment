package org.example.coursclasseservice.repository;

import org.example.coursclasseservice.model.GroupeEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupeEtudiantRepository extends JpaRepository<GroupeEtudiant, Long> {
}
