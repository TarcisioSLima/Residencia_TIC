package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.ufg.dto.PreferenciaRequestDTO;
import org.ufg.dto.PreferenciaResponseDTO;
import org.ufg.entity.Cidade;
import org.ufg.entity.Evento;
import org.ufg.entity.Preferencia;
import org.ufg.entity.Usuario;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class PreferenciaMapperImpl implements PreferenciaMapper {

    @Override
    public Preferencia toEntity(PreferenciaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Preferencia preferencia = new Preferencia();

        preferencia.setEvento( PreferenciaMappersAuxiliares.mapEvento( dto.getIdEvento() ) );
        preferencia.setCidade( PreferenciaMappersAuxiliares.mapCidade( dto.getIdCidade() ) );
        preferencia.setValor( dto.getValor() );
        preferencia.setPersonalizavel( dto.getPersonalizavel() );

        return preferencia;
    }

    @Override
    public PreferenciaResponseDTO toResponseDTO(Preferencia entity) {
        if ( entity == null ) {
            return null;
        }

        PreferenciaResponseDTO preferenciaResponseDTO = new PreferenciaResponseDTO();

        preferenciaResponseDTO.setIdUsuario( entityUsuarioId( entity ) );
        preferenciaResponseDTO.setIdEvento( entityEventoId( entity ) );
        preferenciaResponseDTO.setIdCidade( entityCidadeId( entity ) );
        preferenciaResponseDTO.setId( entity.getId() );
        preferenciaResponseDTO.setDataCriacao( entity.getDataCriacao() );
        preferenciaResponseDTO.setDataUltimaEdicao( entity.getDataUltimaEdicao() );
        preferenciaResponseDTO.setValor( entity.getValor() );
        preferenciaResponseDTO.setPersonalizavel( entity.getPersonalizavel() );

        return preferenciaResponseDTO;
    }

    @Override
    public List<PreferenciaResponseDTO> toResponseDTOList(List<Preferencia> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PreferenciaResponseDTO> list = new ArrayList<PreferenciaResponseDTO>( entities.size() );
        for ( Preferencia preferencia : entities ) {
            list.add( toResponseDTO( preferencia ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(PreferenciaRequestDTO dto, Preferencia entity) {
        if ( dto == null ) {
            return;
        }

        entity.setValor( dto.getValor() );
        entity.setPersonalizavel( dto.getPersonalizavel() );
    }

    private UUID entityUsuarioId(Preferencia preferencia) {
        if ( preferencia == null ) {
            return null;
        }
        Usuario usuario = preferencia.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        UUID id = usuario.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID entityEventoId(Preferencia preferencia) {
        if ( preferencia == null ) {
            return null;
        }
        Evento evento = preferencia.getEvento();
        if ( evento == null ) {
            return null;
        }
        UUID id = evento.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID entityCidadeId(Preferencia preferencia) {
        if ( preferencia == null ) {
            return null;
        }
        Cidade cidade = preferencia.getCidade();
        if ( cidade == null ) {
            return null;
        }
        UUID id = cidade.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
