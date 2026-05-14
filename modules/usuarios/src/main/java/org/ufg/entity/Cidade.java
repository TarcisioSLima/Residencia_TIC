package org.ufg.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.UUID;

@Entity
@Table(name = "cidades")
@BatchSize(size = 50)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Cidade extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    public UUID id;

    @Column(name = "nome", nullable = false, length = 100)
    public String nome;
}
