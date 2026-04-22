package iusjc_planning.user_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "administrateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends User {

    // Optionnel, adapté à ton diagramme
    private String matricule;
}
