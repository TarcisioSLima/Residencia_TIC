package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.ufg.dto.EnvioRequestDTO;
import org.ufg.dto.EnvioResponseDTO;
import org.ufg.entity.Aviso;
import org.ufg.entity.Canal;
import org.ufg.entity.Envio;
import org.ufg.entity.PossivelStatus;
import org.ufg.entity.Usuario;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class EnvioMapperImpl implements EnvioMapper {

    @Override
    public Envio toEntity(EnvioRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Envio envio = new Envio();

        envio.setCanal( EnvioMappersAuxiliares.mapCanal( dto.getIdCanal() ) );
        envio.setAviso( EnvioMappersAuxiliares.mapAviso( dto.getIdAviso() ) );
        envio.setUsuarioDestinatario( EnvioMappersAuxiliares.mapUsuario( dto.getIdUsuarioDestinatario() ) );
        envio.setStatus( EnvioMappersAuxiliares.mapStatus( dto.getIdStatus() ) );

        return envio;
    }

    @Override
    public List<Envio> toEntityList(List<EnvioRequestDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Envio> list = new ArrayList<Envio>( dtos.size() );
        for ( EnvioRequestDTO envioRequestDTO : dtos ) {
            list.add( toEntity( envioRequestDTO ) );
        }

        return list;
    }

    @Override
    public EnvioResponseDTO toResponseDTO(Envio entity) {
        if ( entity == null ) {
            return null;
        }

        EnvioResponseDTO envioResponseDTO = new EnvioResponseDTO();

        envioResponseDTO.setIdCanal( entityCanalId( entity ) );
        envioResponseDTO.setIdAviso( entityAvisoId( entity ) );
        envioResponseDTO.setIdUsuarioDestinatario( entityUsuarioDestinatarioId( entity ) );
        envioResponseDTO.setIdStatus( entityStatusId( entity ) );
        envioResponseDTO.setId( entity.getId() );

        return envioResponseDTO;
    }

    @Override
    public List<EnvioResponseDTO> toResponseDTOList(List<Envio> entities) {
        if ( entities == null ) {
            return null;
        }

        List<EnvioResponseDTO> list = new ArrayList<EnvioResponseDTO>( entities.size() );
        for ( Envio envio : entities ) {
            list.add( toResponseDTO( envio ) );
        }

        return list;
    }

    private UUID entityCanalId(Envio envio) {
        if ( envio == null ) {
            return null;
        }
        Canal canal = envio.getCanal();
        if ( canal == null ) {
            return null;
        }
        UUID id = canal.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID entityAvisoId(Envio envio) {
        if ( envio == null ) {
            return null;
        }
        Aviso aviso = envio.getAviso();
        if ( aviso == null ) {
            return null;
        }
        UUID id = aviso.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID entityUsuarioDestinatarioId(Envio envio) {
        if ( envio == null ) {
            return null;
        }
        Usuario usuarioDestinatario = envio.getUsuarioDestinatario();
        if ( usuarioDestinatario == null ) {
            return null;
        }
        UUID id = usuarioDestinatario.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID entityStatusId(Envio envio) {
        if ( envio == null ) {
            return null;
        }
        PossivelStatus status = envio.getStatus();
        if ( status == null ) {
            return null;
        }
        UUID id = status.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
