package org.example.specialeventservice.repository;

import org.example.specialeventservice.model.DemandeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeEventRepository extends JpaRepository<DemandeEvent, Long> {
}
