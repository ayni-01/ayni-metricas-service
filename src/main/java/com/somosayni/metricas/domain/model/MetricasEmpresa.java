package com.somosayni.metricas.domain.model;

public record MetricasEmpresa(
        String empresaId,
        int retosActivos,
        int nuevasPostulaciones,
        int talentosEvaluados,
        int talentosAprobados
) {}
