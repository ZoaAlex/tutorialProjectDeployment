package org.example.coursclasseservice.repository;

import org.example.coursclasseservice.model.Ue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UeRepository extends JpaRepository<Ue, Long> {
    Optional<Ue> findByCodeUe(String codeUe);
}
