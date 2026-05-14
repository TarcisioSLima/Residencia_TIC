package org.ufg.service;

import io.quarkus.panache.common.Page;
import io.quarkus.redis.datasource.RedisDataSource;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.ufg.dto.AvisoDTO;
import org.ufg.dto.AvisoResponseDTO;
import org.ufg.dto.EstatisticaDTO;
import org.ufg.dto.ForceResendResponseDTO;
import org.ufg.dto.PageDTO;
import org.ufg.entity.Aviso;
import org.ufg.mapper.AvisoMapper;
import org.ufg.repository.AvisoRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AvisoService {

    private static final Logger LOG = Logger.getLogger(AvisoService.class);

    private static final String NOTIFICATIONS_QUEUE = "etl:notifications:ready";

    @Inject
    AvisoRepository avisoRepository;

    @Inject
    AvisoMapper avisoMapper;

    @Inject
    RedisDataSource redisDataSource;

    @Transactional
    public AvisoResponseDTO criarAviso(AvisoDTO avisoDTO) {
        LOG.infof("Service: Método criarAviso chamado.");

        Aviso entity = avisoMapper.toEntity(avisoDTO);
        avisoRepository.persist(entity);

        AvisoResponseDTO criadoDTO = avisoMapper.toResponseDTO(entity);

        LOG.infof("Service: Aviso criado com ID: %s", criadoDTO.getId());

        return criadoDTO;
    }

    public AvisoResponseDTO buscarAvisoPorId(UUID id) {
        LOG.infof("Service: Método buscarAvisoPorId chamado para ID: %s", id);

        Aviso entity = avisoRepository.findById(id);

        if (entity == null) {
            LOG.warnf("Service: Aviso com ID %s não encontrado.", id);
            return null;
        }

        AvisoResponseDTO encontradoDTO = avisoMapper.toResponseDTO(entity);

        LOG.infof("Service: Aviso com ID %s encontrado.", id);
        return encontradoDTO;
    }

    public List<AvisoResponseDTO> listarTodosAvisos() {
        LOG.info("Service: Método listarTodosAvisos chamado.");

        List<Aviso> avisos = avisoRepository.listAll();

        List<AvisoResponseDTO> avisosDTO = avisos.stream()
                .map(avisoMapper::toResponseDTO)
                .collect(Collectors.toList());

        LOG.infof("Service: %d avisos listados.", avisosDTO.size());

        return avisosDTO;
    }

    public List<AvisoResponseDTO> listarTodosAvisosDataAtual() {
        LOG.info("Service: Método listarTodosAvisosDataAtual chamado.");

        List<Aviso> avisos = avisoRepository.listAllToday();

        List<AvisoResponseDTO> avisosDTO = avisos.stream()
                .map(avisoMapper::toResponseDTO)
                .collect(Collectors.toList());

        LOG.infof("Service: %d avisos listados.", avisosDTO.size());

        return avisosDTO;
    }

    @Transactional // Garante que toda a operação de lote seja atômica
    public int processarLoteAvisos(List<AvisoDTO> avisos) {
        final int recebidosCount = avisos != null ? avisos.size() : 0;

        LOG.infof("Service: Método processarLoteAvisos chamado. Avisos recebidos: %d", recebidosCount);

        if (recebidosCount == 0) {
            return 0;
        }

        List<Aviso> entities = avisos.stream()
                .map(avisoMapper::toEntity)
                .collect(Collectors.toList());

        avisoRepository.persist(entities);

        final int processadosCount = entities.size();

        LOG.infof("Service: Persistência em lote concluída. Avisos processados: %d", processadosCount);

        return processadosCount;
    }

    public PageDTO<AvisoResponseDTO> filtrarAvisos(LocalDate data, UUID idEvento, UUID idCidade, int page, int size) {
        var panacheQuery = avisoRepository.buscarComFiltros(data, idEvento, idCidade);

        panacheQuery.page(Page.of(page, size));

        List<AvisoResponseDTO> listDTO = panacheQuery.list().stream()
                .map(avisoMapper::toResponseDTO)
                .toList();

        return new PageDTO<>(
                listDTO,
                panacheQuery.count(),
                panacheQuery.pageCount(),
                page,
                size
        );
    }

    public ForceResendResponseDTO forceResendHoje(List<String> userIds, boolean bypassPreferences) {
        List<Aviso> avisos = avisoRepository.listAllToday();

        if (avisos.isEmpty()) {
            LOG.info("Service: forceResendHoje chamado sem avisos para hoje.");
            return new ForceResendResponseDTO(0, userIds != null ? userIds.size() : -1);
        }

        JsonArray alertsArray = new JsonArray();
        for (Aviso aviso : avisos) {
            JsonObject alert = new JsonObject()
                    .put("aviso_id", aviso.id.toString())
                    .put("id_cidade", aviso.cidade.id.toString())
                    .put("id_evento", aviso.evento.id.toString())
                    .put("nome_cidade", aviso.cidade.nome)
                    .put("nome_evento", aviso.evento.nomeEvento)
                    .put("valor", aviso.valor != null ? aviso.valor.doubleValue() : 0.0)
                    .put("unidade_medida", aviso.unidadeMedida)
                    .put("data_referencia", aviso.dataReferencia.toString());

            if (aviso.horario != null) {
                alert.put("horario", aviso.horario.toString());
            }

            alertsArray.add(alert);
        }

        JsonObject payload = new JsonObject()
                .put("execution_id", UUID.randomUUID().toString())
                .put("date", LocalDate.now().toString())
                .put("avisos_count", avisos.size())
                .put("alerts", alertsArray)
                .put("bypass_preferences", bypassPreferences);

        if (userIds != null && !userIds.isEmpty()) {
            payload.put("target_user_ids", new JsonArray(userIds));
        }

        redisDataSource.list(String.class).rpush(NOTIFICATIONS_QUEUE, payload.encode());

        LOG.infof("Service: forceResendHoje publicado na fila. avisos=%d, targetUsers=%s, bypassPreferences=%b",
                avisos.size(), userIds != null ? userIds.size() : "todos", bypassPreferences);

        return new ForceResendResponseDTO(avisos.size(), userIds != null ? userIds.size() : -1);
    }

    public List<EstatisticaDTO> getTopCidades() {
        return avisoRepository.countPorCidade();
    }

    public List<EstatisticaDTO> getDistribuicaoEventos() {
        return avisoRepository.countPorEvento();
    }

    public List<EstatisticaDTO> getTendenciaSemanal() {
        return avisoRepository.countUltimos7Dias();
    }
}