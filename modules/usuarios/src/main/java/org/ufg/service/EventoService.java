package org.ufg.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.ufg.dto.EventoRequestDTO;
import org.ufg.dto.EventoResponseDTO;
import org.ufg.entity.Evento;
import org.ufg.mapper.EventoMapper;
import org.ufg.repository.EventoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EventoService {

    private static final Logger LOG = Logger.getLogger(EventoService.class);

    @Inject
    EventoRepository eventoRepository;

    @Inject
    EventoMapper eventoMapper;

    @Transactional
    public EventoResponseDTO createEvento(EventoRequestDTO dto) {
        LOG.info("Service: Criando evento.");
        validateEventoNameNotExists(dto.getNomeEvento(), null);

        Evento evento = eventoMapper.toEntity(dto);
        eventoRepository.persist(evento);
        LOG.infof("Service: Evento criado com sucesso. ID: %s", evento.id);

        return eventoMapper.toResponseDTO(evento);
    }

    public List<EventoResponseDTO> findAllEventos() {
        LOG.info("Service: Buscando todos os eventos.");
        return eventoMapper.toResponseDTOList(eventoRepository.listAll());
    }

    public EventoResponseDTO findEventoById(UUID id) {
        LOG.infof("Service: Buscando evento ID: %s", id);

        Evento evento = eventoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado com o ID: " + id));

        return eventoMapper.toResponseDTO(evento);
    }

    @Transactional
    public EventoResponseDTO updateEvento(UUID id, EventoRequestDTO dto) {
        LOG.infof("Service: Atualizando evento ID: %s", id);

        Evento evento = eventoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado para atualização com o ID: " + id));

        Optional<Evento> existingByName = eventoRepository.findByNomeEvento(dto.getNomeEvento());
        if (existingByName.isPresent() && !existingByName.get().id.equals(id)) {
            throw new IllegalArgumentException("Outro evento com este nome já existe.");
        }

        eventoMapper.updateEntityFromDto(dto, evento);
        LOG.infof("Service: Evento atualizado com sucesso. ID: %s", id);

        return eventoMapper.toResponseDTO(evento);
    }

    private void validateEventoNameNotExists(String nomeEvento, UUID excludeEventoId) {
        Optional<Evento> existente = eventoRepository.findByNomeEvento(nomeEvento);
        if (existente.isPresent() && (excludeEventoId == null || !existente.get().id.equals(excludeEventoId))) {
            throw new IllegalArgumentException("Evento com este nome já existe.");
        }
    }
}
