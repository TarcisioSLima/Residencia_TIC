package org.ufg.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ufg.dto.EnvioBulkRequestDTO;
import org.ufg.dto.EnvioLogResponseDTO;
import org.ufg.dto.EnvioRequestDTO;
import org.ufg.dto.EnvioResponseDTO;
import org.ufg.dto.PageDTO;
import org.ufg.entity.Aviso;
import org.ufg.entity.Canal;
import org.ufg.entity.Cidade;
import org.ufg.entity.Envio;
import org.ufg.entity.Evento;
import org.ufg.entity.PossivelStatus;
import org.ufg.entity.Usuario;
import org.ufg.mapper.EnvioMapper;
import org.ufg.repository.EnvioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    EnvioRepository envioRepository;

    @Mock
    EnvioMapper envioMapper;

    @InjectMocks
    EnvioService envioService;

    private UUID envioId;
    private Envio envioEntity;
    private EnvioRequestDTO envioRequestDTO;
    private EnvioResponseDTO envioResponseDTO;
    private Aviso aviso;

    @BeforeEach
    void setUp() {
        envioId = UUID.randomUUID();

        envioRequestDTO = new EnvioRequestDTO();

        Canal canal = new Canal();
        canal.id = UUID.randomUUID();
        canal.nomeCanal = "Email";

        Usuario usuario = new Usuario();
        usuario.id = UUID.randomUUID();
        usuario.nome = "Cliente Teste";
        usuario.email = "cliente@cliente.com";
        usuario.whatsapp = "62999999999";

        PossivelStatus status = new PossivelStatus();
        status.id = UUID.randomUUID();
        status.nomeStatus = "ENVIADO";

        Evento evento = new Evento();
        evento.id = UUID.randomUUID();
        evento.nomeEvento = "Chuva";

        Cidade cidade = new Cidade();
        cidade.id = UUID.randomUUID();
        cidade.nome = "Goiania";

        aviso = new Aviso();
        aviso.id = UUID.randomUUID();
        aviso.evento = evento;
        aviso.cidade = cidade;
        aviso.dataGeracao = LocalDate.of(2026, 4, 3);
        aviso.dataReferencia = LocalDate.of(2026, 4, 4);

        envioEntity = new Envio();
        envioEntity.id = envioId;
        envioEntity.canal = canal;
        envioEntity.aviso = aviso;
        envioEntity.usuarioDestinatario = usuario;
        envioEntity.status = status;

        envioResponseDTO = new EnvioResponseDTO();
        envioResponseDTO.setId(envioId);
    }

    @Test
    @DisplayName("Deve criar envio com sucesso quando todas as entidades relacionadas existem")
    void createEnvio_Success() {
        when(envioMapper.toEntity(envioRequestDTO)).thenReturn(envioEntity);
        when(envioMapper.toResponseDTO(envioEntity)).thenReturn(envioResponseDTO);

        EnvioResponseDTO result = envioService.createEnvio(envioRequestDTO);

        assertNotNull(result);
        assertEquals(envioId, result.getId());
        verify(envioRepository, times(1)).persist(envioEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar envio se alguma entidade relacionada for nula")
    void createEnvio_MissingEntity_ThrowsException() {
        envioEntity.canal = null;

        when(envioMapper.toEntity(envioRequestDTO)).thenReturn(envioEntity);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> envioService.createEnvio(envioRequestDTO));

        assertTrue(exception.getMessage().contains("Uma ou mais entidades"));
        verify(envioRepository, never()).persist(any(Envio.class));
    }

    @Test
    @DisplayName("Deve criar envios em lote com sucesso")
    void createEnviosBulk_Success() {
        EnvioBulkRequestDTO bulkDto = new EnvioBulkRequestDTO();
        bulkDto.setEnvios(List.of(envioRequestDTO));

        List<Envio> listaEntidades = List.of(envioEntity);
        List<EnvioResponseDTO> listaResponse = List.of(envioResponseDTO);

        when(envioMapper.toEntityList(bulkDto.getEnvios())).thenReturn(listaEntidades);
        when(envioMapper.toResponseDTOList(listaEntidades)).thenReturn(listaResponse);

        List<EnvioResponseDTO> result = envioService.createEnviosBulk(bulkDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(envioRepository, times(1)).persist(listaEntidades);
    }

    @Test
    @DisplayName("Deve lançar exceção no lote se algum envio tiver entidade nula")
    void createEnviosBulk_MissingEntity_ThrowsException() {
        EnvioBulkRequestDTO bulkDto = new EnvioBulkRequestDTO();
        bulkDto.setEnvios(List.of(envioRequestDTO));

        envioEntity.usuarioDestinatario = null;
        List<Envio> listaEntidades = List.of(envioEntity);

        when(envioMapper.toEntityList(bulkDto.getEnvios())).thenReturn(listaEntidades);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> envioService.createEnviosBulk(bulkDto));

        assertTrue(exception.getMessage().contains("Pelo menos um dos envios contém um ID"));
        verify(envioRepository, never()).persist(any(List.class));
    }

    @Test
    @DisplayName("Deve retornar lista de envios")
    void findAllEnvios_Success() {
        List<Envio> listaEntidades = List.of(envioEntity);
        List<EnvioResponseDTO> listaDtos = List.of(envioResponseDTO);

        when(envioRepository.listAll()).thenReturn(listaEntidades);
        when(envioMapper.toResponseDTOList(listaEntidades)).thenReturn(listaDtos);

        List<EnvioResponseDTO> result = envioService.findAllEnvios();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar envio por ID com sucesso")
    void findEnvioById_Success() {
        when(envioRepository.findByIdOptional(envioId)).thenReturn(Optional.of(envioEntity));
        when(envioMapper.toResponseDTO(envioEntity)).thenReturn(envioResponseDTO);

        EnvioResponseDTO result = envioService.findEnvioById(envioId);

        assertNotNull(result);
        assertEquals(envioId, result.getId());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar ID inexistente")
    void findEnvioById_NotFound() {
        when(envioRepository.findByIdOptional(envioId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> envioService.findEnvioById(envioId));

        assertTrue(exception.getMessage().contains("Envio não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar logs de envio paginados com dados para a tela administrativa")
    void filtrarLogsEnvio_Success() {
        @SuppressWarnings("unchecked")
        PanacheQuery<Envio> panacheQuery = mock(PanacheQuery.class);

        when(envioRepository.buscarLogsComFiltros(aviso.dataReferencia, aviso.evento.id, aviso.cidade.id)).thenReturn(panacheQuery);
        when(panacheQuery.page(any())).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of(envioEntity));
        when(panacheQuery.count()).thenReturn(1L);
        when(panacheQuery.pageCount()).thenReturn(1);

        PageDTO<EnvioLogResponseDTO> result = envioService.filtrarLogsEnvio(
                aviso.dataReferencia,
                aviso.evento.id,
                aviso.cidade.id,
                0,
                10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Email", result.getContent().get(0).getNomeCanal());
        assertEquals("Cliente Teste", result.getContent().get(0).getNomeUsuarioDestinatario());
        assertEquals("Chuva", result.getContent().get(0).getNomeEvento());
        assertEquals("Goiania", result.getContent().get(0).getNomeCidade());
        verify(envioRepository).buscarLogsComFiltros(aviso.dataReferencia, aviso.evento.id, aviso.cidade.id);
        verify(panacheQuery).page(any());
    }
}
