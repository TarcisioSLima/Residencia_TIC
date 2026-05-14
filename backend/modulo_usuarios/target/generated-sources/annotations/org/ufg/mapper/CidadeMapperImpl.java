package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ufg.dto.CidadeRequestDTO;
import org.ufg.dto.CidadeResponseDTO;
import org.ufg.entity.Cidade;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class CidadeMapperImpl implements CidadeMapper {

    @Override
    public Cidade toEntity(CidadeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cidade cidade = new Cidade();

        cidade.setNome( dto.getNome() );

        return cidade;
    }

    @Override
    public List<Cidade> toEntityList(List<CidadeRequestDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Cidade> list = new ArrayList<Cidade>( dtos.size() );
        for ( CidadeRequestDTO cidadeRequestDTO : dtos ) {
            list.add( toEntity( cidadeRequestDTO ) );
        }

        return list;
    }

    @Override
    public CidadeResponseDTO toResponseDTO(Cidade entity) {
        if ( entity == null ) {
            return null;
        }

        CidadeResponseDTO cidadeResponseDTO = new CidadeResponseDTO();

        cidadeResponseDTO.setId( entity.getId() );
        cidadeResponseDTO.setNome( entity.getNome() );

        return cidadeResponseDTO;
    }

    @Override
    public List<CidadeResponseDTO> toResponseDTOList(List<Cidade> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CidadeResponseDTO> list = new ArrayList<CidadeResponseDTO>( entities.size() );
        for ( Cidade cidade : entities ) {
            list.add( toResponseDTO( cidade ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(CidadeRequestDTO dto, Cidade entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNome( dto.getNome() );
    }
}
