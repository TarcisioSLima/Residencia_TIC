package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ufg.dto.EventoRequestDTO;
import org.ufg.dto.EventoResponseDTO;
import org.ufg.entity.Evento;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class EventoMapperImpl implements EventoMapper {

    @Override
    public Evento toEntity(EventoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Evento evento = new Evento();

        evento.setNomeEvento( dto.getNomeEvento() );
        evento.setPersonalizavel( dto.getPersonalizavel() );
        evento.setHorario( dto.getHorario() );

        return evento;
    }

    @Override
    public EventoResponseDTO toResponseDTO(Evento entity) {
        if ( entity == null ) {
            return null;
        }

        EventoResponseDTO eventoResponseDTO = new EventoResponseDTO();

        eventoResponseDTO.setId( entity.getId() );
        eventoResponseDTO.setNomeEvento( entity.getNomeEvento() );
        eventoResponseDTO.setPersonalizavel( entity.getPersonalizavel() );
        eventoResponseDTO.setHorario( entity.getHorario() );

        return eventoResponseDTO;
    }

    @Override
    public List<EventoResponseDTO> toResponseDTOList(List<Evento> entities) {
        if ( entities == null ) {
            return null;
        }

        List<EventoResponseDTO> list = new ArrayList<EventoResponseDTO>( entities.size() );
        for ( Evento evento : entities ) {
            list.add( toResponseDTO( evento ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(EventoRequestDTO dto, Evento entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNomeEvento( dto.getNomeEvento() );
        entity.setPersonalizavel( dto.getPersonalizavel() );
        entity.setHorario( dto.getHorario() );
    }
}
