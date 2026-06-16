package com.somosayni.metricas.application.query;

import com.somosayni.metricas.domain.model.MetricasEmpresa;
import com.somosayni.metricas.infrastructure.persistence.entity.PostulacionSnapshot;
import com.somosayni.metricas.infrastructure.persistence.entity.RetoSnapshot;
import com.somosayni.metricas.infrastructure.persistence.repository.JpaPostulacionSnapshotRepository;
import com.somosayni.metricas.infrastructure.persistence.repository.JpaRetoSnapshotRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ObtenerMetricasEmpresaQueryHandler {

    private final JpaRetoSnapshotRepository retoRepository;
    private final JpaPostulacionSnapshotRepository postulacionRepository;

    public ObtenerMetricasEmpresaQueryHandler(
            JpaRetoSnapshotRepository retoRepository,
            JpaPostulacionSnapshotRepository postulacionRepository) {
        this.retoRepository = retoRepository;
        this.postulacionRepository = postulacionRepository;
    }

    public MetricasEmpresa handle(ObtenerMetricasEmpresaQuery query) {
        List<RetoSnapshot> retosActivos = retoRepository
                .findByEmpresaIdAndEstado(query.empresaId(), RetoSnapshot.EstadoReto.ACTIVO);

        int nuevasPostulaciones = retosActivos.stream()
                .mapToInt(r -> postulacionRepository.findByRetoId(r.getId()).size())
                .sum();

        int talentosAprobados = retosActivos.stream()
                .mapToInt(r -> (int) postulacionRepository
                        .countByRetoIdAndEstado(r.getId(), PostulacionSnapshot.EstadoPostulacion.APROBADO))
                .sum();

        return new MetricasEmpresa(
                query.empresaId(),
                retosActivos.size(),
                nuevasPostulaciones,
                nuevasPostulaciones,
                talentosAprobados
        );
    }
}
