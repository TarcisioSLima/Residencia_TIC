package org.ufg.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ufg.dto.UsuarioDetalhadoResponseDTO;
import org.ufg.dto.UsuarioRequestDTO;
import org.ufg.dto.UsuarioResponseDTO;
import org.ufg.entity.Usuario;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-25T17:50:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@ApplicationScoped
public class UsuarioMapperImpl implements UsuarioMapper {

    @Inject
    private CanalMapper canalMapper;

    @Override
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setNome( dto.getNome() );
        usuario.setEmail( dto.getEmail() );
        usuario.setWhatsapp( dto.getWhatsapp() );
        usuario.setSenha( dto.getSenha() );
        usuario.setNivelAcesso( dto.getNivelAcesso() );

        return usuario;
    }

    @Override
    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();

        usuarioResponseDTO.setId( entity.getId() );
        usuarioResponseDTO.setNome( entity.getNome() );
        usuarioResponseDTO.setEmail( entity.getEmail() );
        usuarioResponseDTO.setWhatsapp( entity.getWhatsapp() );
        usuarioResponseDTO.setDataCriacao( entity.getDataCriacao() );
        usuarioResponseDTO.setDataUltimaEdicao( entity.getDataUltimaEdicao() );
        usuarioResponseDTO.setNivelAcesso( entity.getNivelAcesso() );
        usuarioResponseDTO.setCanaisPreferidos( canalMapper.toResponseDTOList( entity.getCanaisPreferidos() ) );

        return usuarioResponseDTO;
    }

    @Override
    public List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UsuarioResponseDTO> list = new ArrayList<UsuarioResponseDTO>( entities.size() );
        for ( Usuario usuario : entities ) {
            list.add( toResponseDTO( usuario ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(UsuarioRequestDTO dto, Usuario entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNome( dto.getNome() );
        entity.setEmail( dto.getEmail() );
        entity.setWhatsapp( dto.getWhatsapp() );
        entity.setNivelAcesso( dto.getNivelAcesso() );
    }

    @Override
    public UsuarioDetalhadoResponseDTO toDetalhadoResponseDTO(Usuario entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioDetalhadoResponseDTO usuarioDetalhadoResponseDTO = new UsuarioDetalhadoResponseDTO();

        usuarioDetalhadoResponseDTO.setCanaisPreferidos( canalMapper.toResponseDTOList( entity.getCanaisPreferidos() ) );
        usuarioDetalhadoResponseDTO.setId( entity.getId() );
        usuarioDetalhadoResponseDTO.setNome( entity.getNome() );
        usuarioDetalhadoResponseDTO.setEmail( entity.getEmail() );
        usuarioDetalhadoResponseDTO.setWhatsapp( entity.getWhatsapp() );
        usuarioDetalhadoResponseDTO.setDataCriacao( entity.getDataCriacao() );
        usuarioDetalhadoResponseDTO.setDataUltimaEdicao( entity.getDataUltimaEdicao() );
        usuarioDetalhadoResponseDTO.setNivelAcesso( entity.getNivelAcesso() );

        usuarioDetalhadoResponseDTO.setCidadeId( entity.getPreferencias().iterator().next().getCidade().getId() );
        usuarioDetalhadoResponseDTO.setCidadeNome( entity.getPreferencias().iterator().next().getCidade().getNome() );
        usuarioDetalhadoResponseDTO.setEventoId( entity.getPreferencias().iterator().next().getEvento().getId() );
        usuarioDetalhadoResponseDTO.setEventoNome( entity.getPreferencias().iterator().next().getEvento().getNomeEvento() );
        usuarioDetalhadoResponseDTO.setValor( entity.getPreferencias().iterator().next().getValor() );
        usuarioDetalhadoResponseDTO.setPersonalizavel( entity.getPreferencias().iterator().next().getPersonalizavel() );

        return usuarioDetalhadoResponseDTO;
    }

    @Override
    public List<UsuarioDetalhadoResponseDTO> toDetalhadoResponseDTOList(List<Usuario> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UsuarioDetalhadoResponseDTO> list = new ArrayList<UsuarioDetalhadoResponseDTO>( entities.size() );
        for ( Usuario usuario : entities ) {
            list.add( toDetalhadoResponseDTO( usuario ) );
        }

        return list;
    }
}
