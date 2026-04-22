package org.example.specialeventservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.specialeventservice.model.Enum.TypeSpecialEvent;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private Date debutEvent;

    @Column(nullable = false)
    private Date finEvent;

    @Enumerated(EnumType.STRING)
    private TypeSpecialEvent typeSpecialEvent;

    @OneToOne
    private DemandeEvent demandeEvent;
}
