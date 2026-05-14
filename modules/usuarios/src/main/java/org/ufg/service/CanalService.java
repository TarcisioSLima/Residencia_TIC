package org.ufg.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.ufg.dto.CanalRequestDTO;
import org.ufg.dto.CanalResponseDTO;
import org.ufg.entity.Canal;
import org.ufg.mapper.CanalMapper;
import org.ufg.repository.CanalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CanalService {

    private static final Logger LOG = Logger.getLogger(CanalService.class);

    @Inject
    CanalRepository canalRepository;

    @Inject
    CanalMapper canalMapper;

    @Inject
    ValidationService validationService;

    @Transactional
    public CanalResponseDTO createCanal(CanalRequestDTO dto) {
        LOG.info("Service: Criando canal.");

        validator().validateNotBlank(dto.getNomeCanal(), "Nome do canal");
        validator().validateMaxLength(dto.getNomeCanal(), 45, "Nome do canal");
        validateCanalNameNotExists(dto.getNomeCanal());

        Canal canal = canalMapper.toEntity(dto);
        canal.dataInclusao = LocalDate.now();

        canalRepository.persist(canal);
        LOG.infof("Service: Canal criado com sucesso. ID: %s", canal.id);

        return canalMapper.toResponseDTO(canal);
    }

    public List<CanalResponseDTO> findAllCanais() {
        LOG.info("Service: Buscando todos os canais.");
        return canalMapper.toResponseDTOList(canalRepository.listAll());
    }

    public CanalResponseDTO findCanalById(UUID id) {
        LOG.infof("Service: Buscando canal ID: %s", id);

        Canal canal = canalRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Canal não encontrado com o ID: " + id));

        return canalMapper.toResponseDTO(canal);
    }

    @Transactional
    public CanalResponseDTO updateCanal(UUID id, CanalRequestDTO dto) {
        LOG.infof("Service: Atualizando canal ID: %s", id);

        validator().validateNotBlank(dto.getNomeCanal(), "Nome do canal");
        validator().validateMaxLength(dto.getNomeCanal(), 45, "Nome do canal");

        Canal canal = canalRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Canal não encontrado para atualização com o ID: " + id));

        if (!canal.nomeCanal.equals(dto.getNomeCanal())) {
            validateCanalNameNotExists(dto.getNomeCanal());
        }

        canalMapper.updateEntityFromDto(dto, canal);
        LOG.infof("Service: Canal atualizado com sucesso. ID: %s", id);

        return canalMapper.toResponseDTO(canal);
    }

    private void validateCanalNameNotExists(String nomeCanal) {
        if (canalRepository.findByNomeCanal(nomeCanal).isPresent()) {
            throw new IllegalArgumentException("Canal com este nome já existe.");
        }
    }

    private ValidationService validator() {
        if (validationService == null) {
            validationService = new ValidationService();
        }
        return validationService;
    }
}
