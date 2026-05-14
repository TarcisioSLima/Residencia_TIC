package org.ufg.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.ufg.dto.ApplicationLogResponseDTO;
import org.ufg.entity.ApplicationLog;
import org.ufg.exception.ValidationException;
import org.ufg.repository.ApplicationLogRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@ApplicationScoped
public class ApplicationLogService {

    private static final Logger LOG = Logger.getLogger(ApplicationLogService.class);

    @Inject
    ApplicationLogRepository applicationLogRepository;

    public List<ApplicationLogResponseDTO> listLogs(String task, String date) {
        String normalizedTask = validateTask(task);
        LocalDate targetDate = parseDateOrToday(date);

        LOG.infof("Service: Buscando application_logs. task=%s, date=%s", normalizedTask, targetDate);

        return applicationLogRepository.listByTaskAndDate(normalizedTask, targetDate)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private String validateTask(String task) {
        if (task == null || task.isBlank()) {
            throw new ValidationException("O parâmetro task é obrigatório.");
        }
        return task.trim().toLowerCase();
    }

    private LocalDate parseDateOrToday(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException exception) {
            throw new ValidationException("Formato de data inválido. Use yyyy-MM-dd.", exception);
        }
    }

    private ApplicationLogResponseDTO toResponseDTO(ApplicationLog log) {
        ApplicationLogResponseDTO dto = new ApplicationLogResponseDTO();
        dto.setId(log.id);
        dto.setTask(log.task);
        dto.setExecutionId(log.executionId);
        dto.setLevel(resolveLevel(log.status, log.message));
        dto.setMessage(log.message);
        dto.setStatus(log.status);
        dto.setExtra(log.extra);
        dto.setCreatedAt(log.createdAt);
        return dto;
    }

    private String resolveLevel(String status, String message) {
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
        String normalizedMessage = message == null ? "" : message.trim().toUpperCase();

        if (normalizedStatus.contains("ERROR")
                || normalizedStatus.contains("FAIL")
                || normalizedMessage.contains("ERRO")
                || normalizedMessage.contains("EXCEPTION")
                || normalizedMessage.contains("FALHA")) {
            return "error";
        }

        if (normalizedStatus.contains("WARN")
                || normalizedStatus.contains("IN_PROGRESS")
                || normalizedStatus.contains("STARTED")
                || normalizedMessage.contains("AVISO")
                || normalizedMessage.contains("WARN")) {
            return "warn";
        }

        return "info";
    }
}
