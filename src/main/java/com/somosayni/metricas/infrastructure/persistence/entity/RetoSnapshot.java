package com.somosayni.metricas.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reto")
public class RetoSnapshot {

    @Id
    private String id;

    @Column(name = "empresa_id")
    private String empresaId;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private EstadoReto estado;

    public enum EstadoReto {
        BORRADOR, ACTIVO, CERRADO, ARCHIVADO
    }

    public String getId() { return id; }
    public String getEmpresaId() { return empresaId; }
    public String getTitulo() { return titulo; }
    public EstadoReto getEstado() { return estado; }
}
