package iusjc_planning.planning_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "disponibilites_enseignants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteEnseignant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourSemaine jour;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDisponibilite type;

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estDisponible = true;

    @Column(nullable = false)
    private Long enseignantId;


}