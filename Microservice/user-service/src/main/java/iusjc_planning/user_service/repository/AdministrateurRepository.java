package iusjc_planning.user_service.repository;

import iusjc_planning.user_service.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
}
