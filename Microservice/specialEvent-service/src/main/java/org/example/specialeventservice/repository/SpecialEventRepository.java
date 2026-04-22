package org.example.specialeventservice.repository;

import org.example.specialeventservice.model.SpecialEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialEventRepository extends JpaRepository<SpecialEvent, Long> {
}
