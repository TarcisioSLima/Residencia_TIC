package org.ufg.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Envio extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "id_canal", nullable = false)
    public Canal canal;

    @ManyToOne
    @JoinColumn(name = "id_aviso", nullable = false)
    public Aviso aviso;

    @ManyToOne
    @JoinColumn(name = "id_usuario_destinatario", nullable = false)
    public Usuario usuarioDestinatario;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    public PossivelStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public OffsetDateTime createdAt;
}