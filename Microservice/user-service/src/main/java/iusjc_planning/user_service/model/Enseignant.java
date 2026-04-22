package iusjc_planning.user_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enseignants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enseignant extends User {

    @Column(length = 150)
    private String specialite;

    @Column(length = 100)
    private String grade;
}
