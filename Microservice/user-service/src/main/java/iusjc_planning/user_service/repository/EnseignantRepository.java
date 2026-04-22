package iusjc_planning.user_service.repository;

import iusjc_planning.user_service.model.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

    List<Enseignant> findBySpecialite(String specialite);

    List<Enseignant> findByGrade(String grade);

}
