package org.example.specialeventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.specialeventservice.model.Enum.StatutDemande;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeEventDTO {
    private Long id;
    private String titre;
    private String objectif;
    private StatutDemande status;
    private Date debutEvent;
    private Date finEvent;
    private int nbreLimitParticipant;
    private Long enseignantId;
}
