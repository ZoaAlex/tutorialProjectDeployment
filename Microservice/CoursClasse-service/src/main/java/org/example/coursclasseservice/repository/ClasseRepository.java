package org.example.coursclasseservice.repository;

import org.example.coursclasseservice.model.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    Optional<Classe> findByCode(String code);
}
