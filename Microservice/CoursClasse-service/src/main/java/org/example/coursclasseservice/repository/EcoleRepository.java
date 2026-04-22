package org.example.coursclasseservice.repository;

import org.example.coursclasseservice.model.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EcoleRepository extends JpaRepository<Ecole, Long> {
    Optional<Ecole> findByCode(String code);
}
