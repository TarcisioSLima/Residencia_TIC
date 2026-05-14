package org.ufg.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.UUID;

@Entity
@Table(name = "possiveis_status")
@BatchSize(size = 50)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PossivelStatus extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    public UUID id;

    @Column(name = "nome_status", nullable = false, length = 45)
    public String nomeStatus;

    @ManyToOne
    @JoinColumn(name = "id_canal", nullable = false) // FK para CANAIS
    public Canal canal;
}
