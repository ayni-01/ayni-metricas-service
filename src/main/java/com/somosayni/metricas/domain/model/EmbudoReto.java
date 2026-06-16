package com.somosayni.metricas.domain.model;

public record EmbudoReto(
        String retoId,
        String tituloReto,
        int postulados,
        int evaluados,
        int aprobados,
        double tasaConversion
) {}
