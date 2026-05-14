package org.ufg.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.ufg.entity.ApplicationLog;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ApplicationLogRepository implements PanacheRepositoryBase<ApplicationLog, UUID> {

    public List<ApplicationLog> listByTaskAndDate(String taskPrefix, LocalDate date) {
        return find(
                "lower(task) like ?1 and function('date', createdAt) = ?2 order by createdAt desc, id desc",
                taskPrefix + ":%",
                date
        ).list();
    }
}
