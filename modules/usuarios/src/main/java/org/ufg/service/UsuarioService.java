package org.ufg.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.ufg.dto.LoginResponseDTO;
import org.ufg.dto.PerfilUsuarioDTO;
import org.ufg.dto.UsuarioDetalhadoResponseDTO;
import org.ufg.dto.UsuarioRequestDTO;
import org.ufg.dto.UsuarioResponseDTO;
import org.ufg.entity.Canal;
import org.ufg.entity.Usuario;
import org.ufg.mapper.UsuarioMapper;
import org.ufg.repository.CanalRepository;
import org.ufg.repository.UsuarioRepository;
import org.ufg.util.SecurityUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class UsuarioService {

    private static final Logger LOG = Logger.getLogger(UsuarioService.class);

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioMapper usuarioMapper;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    PreferenciaService preferenciaService;

    @Inject
    CanalRepository canalRepository;

    @Inject
    ValidationService validationService;

    @Transactional
    public LoginResponseDTO authenticate(String email, String rawPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new NotFoundException("Credenciais inválidas.");
        }
        if (!securityUtils.verifyPassword(rawPassword, usuario.senha)) {
            throw new ForbiddenException("Credenciais inválidas.");
        }

        // JWT groups e @RolesAllowed usam ADMIN/CLIENTE em maiúsculas; dados legados podem estar em minúsculas.
        String nivelJwt = "ADMIN".equalsIgnoreCase(usuario.nivelAcesso != null ? usuario.nivelAcesso.trim() : "")
                ? "ADMIN"
                : "CLIENTE";
        Set<String> roles = Set.of(nivelJwt);
        String token = securityUtils.generateJwt(usuario.id, usuario.email, roles);
        usuario.loginToken = token;
        usuario.dataUltimaEdicao = LocalDate.now();

        LoginResponseDTO loginResponse = new LoginResponseDTO(token, usuario.id, nivelJwt);
        loginResponse.setNome(usuario.nome);
        loginResponse.setEmail(usuario.email);
        loginResponse.setWhatsapp(usuario.whatsapp);
        return loginResponse;
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto, boolean isCreationByAdmin) {
        LOG.infof("Service: Criando novo usuário com email: %s", dto.getEmail());

        validateEmailNotExists(dto.getEmail(), null);
        if (dto.getWhatsapp() != null && !dto.getWhatsapp().trim().isEmpty()) {
            validateWhatsappNotExists(dto.getWhatsapp(), null);
        }

        Usuario novoUsuario = usuarioMapper.toEntity(dto);
        novoUsuario.senha = securityUtils.hashPassword(dto.getSenha());

        String nivelDesejado = dto.getNivelAcesso() != null ? dto.getNivelAcesso().toUpperCase() : "CLIENTE";
        novoUsuario.nivelAcesso = isCreationByAdmin && "ADMIN".equals(nivelDesejado) ? "ADMIN" : "CLIENTE";

        LocalDateTime agora = LocalDateTime.now();
        novoUsuario.dataCriacao = agora;
        novoUsuario.dataUltimaEdicao = agora.toLocalDate();

        usuarioRepository.persist(novoUsuario);
        return usuarioMapper.toResponseDTO(novoUsuario);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioMapper.toResponseDTOList(usuarioRepository.findAllWithCanais());
    }

    public UsuarioResponseDTO buscarUsuarioPorId(UUID id) {
        Usuario usuario = usuarioRepository.findByIdWithCanais(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário com ID " + id + " não encontrado.");
        }
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(UUID id, UsuarioRequestDTO dto) {
        Usuario usuarioParaAtualizar = findByIdOrThrowNotFound(id);

        if (dto.getEmail() != null && !dto.getEmail().equals(usuarioParaAtualizar.email)) {
            validateEmailNotExists(dto.getEmail(), id);
        }

        if (dto.getWhatsapp() != null && !dto.getWhatsapp().equals(usuarioParaAtualizar.whatsapp)) {
            validateWhatsappNotExists(dto.getWhatsapp(), id);
        }

        usuarioMapper.updateEntityFromDto(dto, usuarioParaAtualizar);

        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuarioParaAtualizar.senha = securityUtils.hashPassword(dto.getSenha());
        }

        usuarioParaAtualizar.dataUltimaEdicao = LocalDate.now();
        return usuarioMapper.toResponseDTO(usuarioParaAtualizar);
    }

    @Transactional
    public UsuarioResponseDTO atualizarNivelAcesso(UUID id, String novoNivelAcesso) {
        if (novoNivelAcesso == null || (!"ADMIN".equals(novoNivelAcesso) && !"CLIENTE".equals(novoNivelAcesso))) {
            throw new BadRequestException("Nível de acesso inválido. Permitido: ADMIN ou CLIENTE.");
        }

        Usuario usuario = findByIdOrThrowNotFound(id);
        usuario.nivelAcesso = novoNivelAcesso;
        usuario.dataUltimaEdicao = LocalDate.now();

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        throw new UnsupportedOperationException();
    }

    public List<UsuarioResponseDTO> findUsuariosByEvento(UUID idEvento) {
        return usuarioMapper.toResponseDTOList(usuarioRepository.findByPreferenciaEventoId(idEvento));
    }

    public List<UsuarioResponseDTO> findUsuariosByEventoAndCidade(UUID idEvento, UUID idCidade) {
        return usuarioMapper.toResponseDTOList(usuarioRepository.findByPreferenciaEventoIdAndCidadeId(idEvento, idCidade));
    }

    public List<UsuarioDetalhadoResponseDTO> findUsuariosDetalhadoByEventoAndCidade(UUID idEvento, UUID idCidade) {
        return usuarioMapper.toDetalhadoResponseDTOList(usuarioRepository.findDetalhadoByEventoAndCidade(idEvento, idCidade));
    }

    @Transactional
    public void atualizarPerfilCompleto(UUID idUsuario, PerfilUsuarioDTO dto) {
        Usuario usuario = findByIdOrThrowNotFound(idUsuario);
        usuario.canaisPreferidos.clear();

        if (dto.getIdsCanaisPreferidos() != null && !dto.getIdsCanaisPreferidos().isEmpty()) {
            List<Canal> canais = canalRepository.list("id in ?1", dto.getIdsCanaisPreferidos());
            usuario.canaisPreferidos.addAll(canais);
        }

        preferenciaService.substituirPreferenciasDoUsuario(usuario, dto.getPreferencias());
        usuario.dataUltimaEdicao = LocalDate.now();
    }

    private void validateEmailNotExists(String email, UUID excludeUserId) {
        Usuario existente = usuarioRepository.findByEmail(email);
        if (existente != null && (excludeUserId == null || existente.id == null || !existente.id.equals(excludeUserId))) {
            throw new BadRequestException("O novo e-mail informado já está cadastrado em outra conta.");
        }
    }

    private void validateWhatsappNotExists(String whatsapp, UUID excludeUserId) {
        Usuario existente = usuarioRepository.findByWhatsapp(whatsapp);
        if (existente != null && (excludeUserId == null || existente.id == null || !existente.id.equals(excludeUserId))) {
            throw new BadRequestException("O número de WhatsApp informado já está cadastrado em outra conta.");
        }
    }

    private Usuario findByIdOrThrowNotFound(UUID id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new NotFoundException("Usuário com ID " + id + " não encontrado.");
        }
        return usuario;
    }
}
