package org.ufg.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.ufg.entity.Envio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class EnvioRepository implements PanacheRepositoryBase<Envio, UUID> {

    public PanacheQuery<Envio> buscarLogsComFiltros(LocalDate data, UUID idEvento, UUID idCidade) {
        StringBuilder where = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (data != null) {
            where.append(" AND a.dataReferencia = :data");
            params.put("data", data);
        }

        if (idEvento != null) {
            where.append(" AND ev.id = :idEvento");
            params.put("idEvento", idEvento);
        }

        if (idCidade != null) {
            where.append(" AND ci.id = :idCidade");
            params.put("idCidade", idCidade);
        }

        String query = "FROM Envio e " +
                "JOIN FETCH e.aviso a " +
                "LEFT JOIN FETCH a.evento ev " +
                "LEFT JOIN FETCH a.cidade ci " +
                "JOIN FETCH e.canal " +
                "JOIN FETCH e.status " +
                "JOIN FETCH e.usuarioDestinatario " +
                "WHERE " + where + " ORDER BY a.dataReferencia DESC, e.createdAt DESC, e.id DESC";

        return find(query, params);
    }

    public long contarLogsComFiltros(LocalDate data, UUID idEvento, UUID idCidade) {
        StringBuilder where = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (data != null) {
            where.append(" AND aviso.dataReferencia = :data");
            params.put("data", data);
        }

        if (idEvento != null) {
            where.append(" AND aviso.evento.id = :idEvento");
            params.put("idEvento", idEvento);
        }

        if (idCidade != null) {
            where.append(" AND aviso.cidade.id = :idCidade");
            params.put("idCidade", idCidade);
        }

        return count(where.toString(), params);
    }

    public long countByStatus(String nomeStatus) {
        return count("status.nomeStatus", nomeStatus);
    }

    public long countEnviadosHoje() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return count("createdAt >= ?1", startOfDay);
    }
}
