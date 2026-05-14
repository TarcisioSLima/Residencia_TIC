package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ufg.dto.CanalRequestDTO;
import org.ufg.dto.CanalResponseDTO;
import org.ufg.entity.Canal;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class CanalMapperImpl implements CanalMapper {

    @Override
    public Canal toEntity(CanalRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Canal canal = new Canal();

        canal.setNomeCanal( dto.getNomeCanal() );

        return canal;
    }

    @Override
    public CanalResponseDTO toResponseDTO(Canal entity) {
        if ( entity == null ) {
            return null;
        }

        CanalResponseDTO canalResponseDTO = new CanalResponseDTO();

        canalResponseDTO.setId( entity.getId() );
        canalResponseDTO.setNomeCanal( entity.getNomeCanal() );
        canalResponseDTO.setDataInclusao( entity.getDataInclusao() );

        return canalResponseDTO;
    }

    @Override
    public List<CanalResponseDTO> toResponseDTOList(List<Canal> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CanalResponseDTO> list = new ArrayList<CanalResponseDTO>( entities.size() );
        for ( Canal canal : entities ) {
            list.add( toResponseDTO( canal ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(CanalRequestDTO dto, Canal entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNomeCanal( dto.getNomeCanal() );
    }
}
