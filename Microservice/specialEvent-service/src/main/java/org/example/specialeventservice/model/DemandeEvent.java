package org.example.specialeventservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.specialeventservice.model.Enum.StatutDemande;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String objectif;

    @Enumerated(EnumType.STRING)
    private StatutDemande status = StatutDemande.EN_ATTENTE;

    @Column(nullable = false)
    private Date debutEvent;

    @Column(nullable = false)
    private Date finEvent;

    @Column(nullable = false)
    private int nbreLimitParticipant;

    @Column(nullable = false)
    private Long enseignantId;
}
