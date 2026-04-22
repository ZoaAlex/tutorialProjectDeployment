package org.example.specialeventservice.mapper;

import org.example.specialeventservice.dto.DemandeEventDTO;
import org.example.specialeventservice.dto.SpecialEventDTO;
import org.example.specialeventservice.model.DemandeEvent;
import org.example.specialeventservice.model.SpecialEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public DemandeEventDTO toDemandeDTO(DemandeEvent entity) {
        if (entity == null)
            return null;
        DemandeEventDTO dto = new DemandeEventDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public DemandeEvent toDemandeEntity(DemandeEventDTO dto) {
        if (dto == null)
            return null;
        DemandeEvent entity = new DemandeEvent();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    public SpecialEventDTO toSpecialDTO(SpecialEvent entity) {
        if (entity == null)
            return null;
        SpecialEventDTO dto = new SpecialEventDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getDemandeEvent() != null) {
            dto.setDemandeEventId(entity.getDemandeEvent().getId());
        }
        return dto;
    }

    public SpecialEvent toSpecialEntity(SpecialEventDTO dto) {
        if (dto == null)
            return null;
        SpecialEvent entity = new SpecialEvent();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
