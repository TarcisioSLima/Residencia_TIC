package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.ufg.dto.AvisoDTO;
import org.ufg.dto.AvisoResponseDTO;
import org.ufg.entity.Aviso;
import org.ufg.entity.Cidade;
import org.ufg.entity.Evento;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class AvisoMapperImpl implements AvisoMapper {

    @Override
    public Aviso toEntity(AvisoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Aviso aviso = new Aviso();

        aviso.setEvento( MappersAuxiliares.mapEvento( dto.getIdEvento() ) );
        aviso.setCidade( MappersAuxiliares.mapCidade( dto.getIdCidade() ) );
        aviso.setId( dto.getId() );
        aviso.setValor( dto.getValor() );
        aviso.setDataGeracao( dto.getDataGeracao() );
        aviso.setDataReferencia( dto.getDataReferencia() );
        aviso.setValorLimite( dto.getValorLimite() );
        aviso.setUnidadeMedida( dto.getUnidadeMedida() );
        aviso.setDiferenca( dto.getDiferenca() );
        aviso.setHorario( dto.getHorario() );
        aviso.setSegundos( dto.getSegundos() );

        return aviso;
    }

    @Override
    public AvisoResponseDTO toResponseDTO(Aviso entity) {
        if ( entity == null ) {
            return null;
        }

        AvisoResponseDTO avisoResponseDTO = new AvisoResponseDTO();

        avisoResponseDTO.setIdEvento( entityEventoId( entity ) );
        avisoResponseDTO.setNomeEvento( entityEventoNomeEvento( entity ) );
        avisoResponseDTO.setIdCidade( entityCidadeId( entity ) );
        avisoResponseDTO.setNomeCidade( entityCidadeNome( entity ) );
        avisoResponseDTO.setId( entity.getId() );
        avisoResponseDTO.setValor( entity.getValor() );
        avisoResponseDTO.setDataGeracao( entity.getDataGeracao() );
        avisoResponseDTO.setDataReferencia( entity.getDataReferencia() );
        avisoResponseDTO.setValorLimite( entity.getValorLimite() );
        avisoResponseDTO.setUnidadeMedida( entity.getUnidadeMedida() );
        avisoResponseDTO.setDiferenca( entity.getDiferenca() );
        avisoResponseDTO.setHorario( entity.getHorario() );
        avisoResponseDTO.setSegundos( entity.getSegundos() );

        return avisoResponseDTO;
    }

    @Override
    public List<AvisoResponseDTO> toResponseDTOList(List<Aviso> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AvisoResponseDTO> list = new ArrayList<AvisoResponseDTO>( entities.size() );
        for ( Aviso aviso : entities ) {
            list.add( toResponseDTO( aviso ) );
        }

        return list;
    }

    private UUID entityEventoId(Aviso aviso) {
        if ( aviso == null ) {
            return null;
        }
        Evento evento = aviso.getEvento();
        if ( evento == null ) {
            return null;
        }
        UUID id = evento.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityEventoNomeEvento(Aviso aviso) {
        if ( aviso == null ) {
            return null;
        }
        Evento evento = aviso.getEvento();
        if ( evento == null ) {
            return null;
        }
        String nomeEvento = evento.getNomeEvento();
        if ( nomeEvento == null ) {
            return null;
        }
        return nomeEvento;
    }

    private UUID entityCidadeId(Aviso aviso) {
        if ( aviso == null ) {
            return null;
        }
        Cidade cidade = aviso.getCidade();
        if ( cidade == null ) {
            return null;
        }
        UUID id = cidade.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityCidadeNome(Aviso aviso) {
        if ( aviso == null ) {
            return null;
        }
        Cidade cidade = aviso.getCidade();
        if ( cidade == null ) {
            return null;
        }
        String nome = cidade.getNome();
        if ( nome == null ) {
            return null;
        }
        return nome;
    }
}
