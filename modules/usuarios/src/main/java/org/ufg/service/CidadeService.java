package org.ufg.service;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.ufg.dto.CidadeBulkRequestDTO;
import org.ufg.dto.CidadeRequestDTO;
import org.ufg.dto.CidadeResponseDTO;
import org.ufg.entity.Cidade;
import org.ufg.exception.ValidationException;
import org.ufg.mapper.CidadeMapper;
import org.ufg.repository.CidadeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CidadeService {

    private static final Logger LOG = Logger.getLogger(CidadeService.class);

    @Inject
    CidadeRepository cidadeRepository;

    @Inject
    CidadeMapper cidadeMapper;

    @Inject
    ValidationService validationService;

    @CacheInvalidateAll(cacheName = "lista-cidades")
    @Transactional
    public CidadeResponseDTO createCidade(CidadeRequestDTO dto) {
        LOG.info("Service: Criando cidade.");

        validator().validateNotBlank(dto.getNome(), "Nome da cidade");
        validator().validateMaxLength(dto.getNome(), 100, "Nome da cidade");
        validateCidadeNameNotExists(dto.getNome(), null);

        Cidade cidade = cidadeMapper.toEntity(dto);
        cidadeRepository.persist(cidade);
        LOG.infof("Service: Cidade criada com sucesso. ID: %s", cidade.id);

        return cidadeMapper.toResponseDTO(cidade);
    }

    @CacheInvalidateAll(cacheName = "lista-cidades")
    @Transactional
    public List<CidadeResponseDTO> createCidadesBulk(CidadeBulkRequestDTO bulkDto) {
        LOG.info("Service: Criando cidades em lote.");

        if (bulkDto.getCidades() == null || bulkDto.getCidades().isEmpty()) {
            throw new ValidationException("A lista de cidades não pode estar vazia.");
        }

        for (CidadeRequestDTO cidadeDto : bulkDto.getCidades()) {
            validator().validateNotBlank(cidadeDto.getNome(), "Nome da cidade");
            validator().validateMaxLength(cidadeDto.getNome(), 100, "Nome da cidade");
        }

        List<Cidade> novasCidades = cidadeMapper.toEntityList(bulkDto.getCidades());

        List<String> nomesDuplicados = novasCidades.stream()
                .map(cidade -> cidade.nome)
                .filter(nome -> cidadeRepository.findByNome(nome).isPresent())
                .collect(Collectors.toList());

        if (!nomesDuplicados.isEmpty()) {
            throw new IllegalArgumentException("As seguintes cidades já existem no banco: " + String.join(", ", nomesDuplicados));
        }

        cidadeRepository.persist(novasCidades);
        LOG.infof("Service: %d cidades criadas em lote com sucesso.", novasCidades.size());

        return cidadeMapper.toResponseDTOList(novasCidades);
    }

    @CacheResult(cacheName = "lista-cidades")
    public List<CidadeResponseDTO> findAllCidades() {
        LOG.info("Service: Buscando todas as cidades.");
        return cidadeMapper.toResponseDTOList(cidadeRepository.listAll());
    }

    public CidadeResponseDTO findCidadeById(UUID id) {
        LOG.infof("Service: Buscando cidade ID: %s", id);

        Cidade cidade = cidadeRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cidade não encontrada com o ID: " + id));

        return cidadeMapper.toResponseDTO(cidade);
    }

    @CacheInvalidateAll(cacheName = "lista-cidades")
    @Transactional
    public CidadeResponseDTO updateCidade(UUID id, CidadeRequestDTO dto) {
        LOG.infof("Service: Atualizando cidade ID: %s", id);

        validator().validateNotBlank(dto.getNome(), "Nome da cidade");
        validator().validateMaxLength(dto.getNome(), 100, "Nome da cidade");

        Optional<Cidade> existingByName = cidadeRepository.findByNome(dto.getNome());
        if (existingByName.isPresent() && !existingByName.get().id.equals(id)) {
            throw new IllegalArgumentException("Outra cidade com este nome já existe.");
        }

        Cidade cidade = cidadeRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cidade não encontrada para atualização com o ID: " + id));

        cidadeMapper.updateEntityFromDto(dto, cidade);
        LOG.infof("Service: Cidade atualizada com sucesso. ID: %s", id);

        return cidadeMapper.toResponseDTO(cidade);
    }

    private void validateCidadeNameNotExists(String nome, UUID excludeCidadeId) {
        Optional<Cidade> existente = cidadeRepository.findByNome(nome);
        if (existente.isPresent() && (excludeCidadeId == null || !existente.get().id.equals(excludeCidadeId))) {
            throw new IllegalArgumentException("Cidade com este nome já existe.");
        }
    }

    private ValidationService validator() {
        if (validationService == null) {
            validationService = new ValidationService();
        }
        return validationService;
    }
}
