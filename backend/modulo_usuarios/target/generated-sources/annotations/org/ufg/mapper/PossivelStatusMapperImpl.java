package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.ufg.dto.PossivelStatusRequestDTO;
import org.ufg.dto.PossivelStatusResponseDTO;
import org.ufg.entity.Canal;
import org.ufg.entity.PossivelStatus;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class PossivelStatusMapperImpl implements PossivelStatusMapper {

    @Override
    public PossivelStatus toEntity(PossivelStatusRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PossivelStatus possivelStatus = new PossivelStatus();

        possivelStatus.setCanal( PossivelStatusMappersAuxiliares.mapCanal( dto.getIdCanal() ) );
        possivelStatus.setNomeStatus( dto.getNomeStatus() );

        return possivelStatus;
    }

    @Override
    public PossivelStatusResponseDTO toResponseDTO(PossivelStatus entity) {
        if ( entity == null ) {
            return null;
        }

        PossivelStatusResponseDTO possivelStatusResponseDTO = new PossivelStatusResponseDTO();

        possivelStatusResponseDTO.setIdCanal( entityCanalId( entity ) );
        possivelStatusResponseDTO.setId( entity.getId() );
        possivelStatusResponseDTO.setNomeStatus( entity.getNomeStatus() );

        return possivelStatusResponseDTO;
    }

    @Override
    public List<PossivelStatusResponseDTO> toResponseDTOList(List<PossivelStatus> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PossivelStatusResponseDTO> list = new ArrayList<PossivelStatusResponseDTO>( entities.size() );
        for ( PossivelStatus possivelStatus : entities ) {
            list.add( toResponseDTO( possivelStatus ) );
        }

        return list;
    }

    private UUID entityCanalId(PossivelStatus possivelStatus) {
        if ( possivelStatus == null ) {
            return null;
        }
        Canal canal = possivelStatus.getCanal();
        if ( canal == null ) {
            return null;
        }
        UUID id = canal.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
