package org.ufg.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApplicationLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    public UUID id;

    @Column(name = "task", nullable = false, length = 100)
    public String task;

    @Column(name = "execution_id", nullable = false)
    public UUID executionId;

    @Column(name = "message", nullable = false, length = 255)
    public String message;

    @Column(name = "status", length = 20)
    public String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra", columnDefinition = "jsonb")
    public String extra;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
