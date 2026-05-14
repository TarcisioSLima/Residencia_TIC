package org.ufg.service;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.ufg.dto.EnvioBulkRequestDTO;
import org.ufg.dto.EnvioLogResponseDTO;
import org.ufg.dto.EnvioRequestDTO;
import org.ufg.dto.EnvioResponseDTO;
import org.ufg.dto.PageDTO;
import org.ufg.entity.Envio;
import org.ufg.exception.ValidationException;
import org.ufg.mapper.EnvioMapper;
import org.ufg.repository.EnvioRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class EnvioService {

    private static final Logger LOG = Logger.getLogger(EnvioService.class);

    @Inject
    EnvioRepository envioRepository;

    @Inject
    EnvioMapper envioMapper;

    @Transactional
    public EnvioResponseDTO createEnvio(EnvioRequestDTO dto) {
        LOG.info("Service: Criando envio.");

        Envio envio = envioMapper.toEntity(dto);
        validateMappedEnvio(envio, false);

        envioRepository.persist(envio);
        LOG.infof("Service: Envio criado com sucesso. ID: %s", envio.id);

        return envioMapper.toResponseDTO(envio);
    }

    @Transactional
    public List<EnvioResponseDTO> createEnviosBulk(EnvioBulkRequestDTO bulkDto) {
        LOG.info("Service: Criando envios em lote.");

        if (bulkDto.getEnvios() == null || bulkDto.getEnvios().isEmpty()) {
            throw new ValidationException("A lista de envios não pode estar vazia.");
        }

        List<Envio> novosEnvios = envioMapper.toEntityList(bulkDto.getEnvios());
        for (Envio envio : novosEnvios) {
            validateMappedEnvio(envio, true);
        }

        envioRepository.persist(novosEnvios);
        LOG.infof("Service: %d envios criados em lote com sucesso.", novosEnvios.size());

        return envioMapper.toResponseDTOList(novosEnvios);
    }

    public List<EnvioResponseDTO> findAllEnvios() {
        LOG.info("Service: Buscando todos os envios.");
        return envioMapper.toResponseDTOList(envioRepository.listAll());
    }

    public EnvioResponseDTO findEnvioById(UUID id) {
        LOG.infof("Service: Buscando envio por ID: %s", id);

        Envio envio = envioRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Envio não encontrado com o ID: " + id));

        return envioMapper.toResponseDTO(envio);
    }

    public PageDTO<EnvioLogResponseDTO> filtrarLogsEnvio(LocalDate data, UUID idEvento, UUID idCidade, int page, int size) {
        LOG.infof("Service: Buscando logs de envio. data=%s, idEvento=%s, idCidade=%s, page=%d, size=%d",
                data, idEvento, idCidade, page, size);

        var panacheQuery = envioRepository.buscarLogsComFiltros(data, idEvento, idCidade);
        panacheQuery.page(Page.of(page, size));

        List<EnvioLogResponseDTO> listDTO = panacheQuery.list().stream()
                .map(this::toLogResponseDTO)
                .toList();

        long total = envioRepository.contarLogsComFiltros(data, idEvento, idCidade);
        int pageCount = size > 0 ? (int) Math.ceil((double) total / size) : 1;

        return new PageDTO<>(
                listDTO,
                total,
                pageCount,
                page,
                size
        );
    }

    public Map<String, Long> getMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalEnvios", envioRepository.count());
        metrics.put("enviadosHoje", envioRepository.countEnviadosHoje());
        metrics.put("falhas", envioRepository.countByStatus("FALHA"));
        metrics.put("pendentes", envioRepository.countByStatus("PENDENTE"));
        return metrics;
    }

    private void validateMappedEnvio(Envio envio, boolean bulk) {
        if (envio.canal == null || envio.aviso == null || envio.usuarioDestinatario == null || envio.status == null) {
            if (bulk) {
                throw new NotFoundException("Pelo menos um dos envios contém um ID de entidade (Canal, Aviso, Usuário ou Status) não encontrado.");
            }
            throw new NotFoundException("Uma ou mais entidades (Canal, Aviso, Usuário ou Status) não foram encontradas ou estão inválidas.");
        }
    }

    private EnvioLogResponseDTO toLogResponseDTO(Envio envio) {
        EnvioLogResponseDTO dto = new EnvioLogResponseDTO();
        dto.setId(envio.id);
        dto.setIdAviso(envio.aviso.id);
        dto.setIdCanal(envio.canal.id);
        dto.setNomeCanal(envio.canal.nomeCanal);
        dto.setIdStatus(envio.status.id);
        dto.setNomeStatus(envio.status.nomeStatus);
        dto.setIdUsuarioDestinatario(envio.usuarioDestinatario.id);
        dto.setNomeUsuarioDestinatario(envio.usuarioDestinatario.nome);
        dto.setEmailUsuarioDestinatario(envio.usuarioDestinatario.email);
        dto.setWhatsappUsuarioDestinatario(envio.usuarioDestinatario.whatsapp);
        dto.setIdEvento(envio.aviso.evento != null ? envio.aviso.evento.id : null);
        dto.setNomeEvento(envio.aviso.evento != null ? envio.aviso.evento.nomeEvento : null);
        dto.setIdCidade(envio.aviso.cidade != null ? envio.aviso.cidade.id : null);
        dto.setNomeCidade(envio.aviso.cidade != null ? envio.aviso.cidade.nome : null);
        dto.setDataGeracaoAviso(envio.aviso.dataGeracao);
        dto.setDataReferenciaAviso(envio.aviso.dataReferencia);
        dto.setCreatedAt(envio.createdAt);
        return dto;
    }
}
