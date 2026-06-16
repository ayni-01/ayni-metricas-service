package com.somosayni.metricas.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "postulacion")
public class PostulacionSnapshot {

    @Id
    private String id;

    @Column(name = "reto_id")
    private String retoId;

    @Enumerated(EnumType.STRING)
    private EstadoPostulacion estado;

    public enum EstadoPostulacion {
        EN_REVISION, APROBADO, RECHAZADO
    }

    public String getId() { return id; }
    public String getRetoId() { return retoId; }
    public EstadoPostulacion getEstado() { return estado; }
}
