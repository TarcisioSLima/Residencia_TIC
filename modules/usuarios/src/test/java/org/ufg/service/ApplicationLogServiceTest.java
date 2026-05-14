package org.ufg.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ufg.dto.ApplicationLogResponseDTO;
import org.ufg.entity.ApplicationLog;
import org.ufg.exception.ValidationException;
import org.ufg.repository.ApplicationLogRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationLogServiceTest {

    @Mock
    ApplicationLogRepository applicationLogRepository;

    @InjectMocks
    ApplicationLogService applicationLogService;

    @Test
    @DisplayName("Deve retornar logs filtrados por task e data com nível derivado")
    void listLogs_Success() {
        ApplicationLog log = new ApplicationLog();
        log.id = UUID.randomUUID();
        log.task = "etl";
        log.executionId = UUID.randomUUID();
        log.message = "Falha ao persistir registros";
        log.status = "ERROR";
        log.extra = "{\"step\":\"load\"}";
        log.createdAt = OffsetDateTime.of(2026, 3, 9, 10, 30, 0, 0, ZoneOffset.UTC);

        when(applicationLogRepository.listByTaskAndDate("etl", LocalDate.of(2026, 3, 9)))
                .thenReturn(List.of(log));

        List<ApplicationLogResponseDTO> result = applicationLogService.listLogs("etl", "2026-03-09");

        assertEquals(1, result.size());
        assertEquals("etl", result.get(0).getTask());
        assertEquals("error", result.get(0).getLevel());
        assertEquals("ERROR", result.get(0).getStatus());
        verify(applicationLogRepository).listByTaskAndDate("etl", LocalDate.of(2026, 3, 9));
    }

    @Test
    @DisplayName("Deve lançar erro quando task não for informada")
    void listLogs_MissingTask_ThrowsValidationException() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> applicationLogService.listLogs("   ", "2026-03-09")
        );

        assertTrue(exception.getMessage().contains("task é obrigatório"));
        verify(applicationLogRepository, never()).listByTaskAndDate(any(), any());
    }

    @Test
    @DisplayName("Deve lançar erro quando data estiver em formato inválido")
    void listLogs_InvalidDate_ThrowsValidationException() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> applicationLogService.listLogs("etl", "09-03-2026")
        );

        assertTrue(exception.getMessage().contains("Formato de data inválido"));
        verify(applicationLogRepository, never()).listByTaskAndDate(any(), any());
    }

    @Test
    @DisplayName("Deve usar a data atual quando o parâmetro date não for informado")
    void listLogs_UsesCurrentDate_WhenDateMissing() {
        when(applicationLogRepository.listByTaskAndDate(eq("etl"), eq(LocalDate.now())))
                .thenReturn(List.of());

        List<ApplicationLogResponseDTO> result = applicationLogService.listLogs("etl", null);

        assertTrue(result.isEmpty());
        verify(applicationLogRepository).listByTaskAndDate("etl", LocalDate.now());
    }
}
