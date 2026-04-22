package org.example.specialeventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.specialeventservice.model.Enum.TypeSpecialEvent;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialEventDTO {
    private Long id;
    private String titre;
    private Date debutEvent;
    private Date finEvent;
    private TypeSpecialEvent typeSpecialEvent;
    private Long demandeEventId;
}
