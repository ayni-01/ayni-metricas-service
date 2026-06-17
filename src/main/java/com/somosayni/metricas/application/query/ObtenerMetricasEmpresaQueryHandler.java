package com.somosayni.metricas.application.query;

import com.somosayni.metricas.domain.model.MetricasEmpresa;
import com.somosayni.metricas.infrastructure.persistence.entity.PostulacionSnapshot;
import com.somosayni.metricas.infrastructure.persistence.entity.RetoSnapshot;
import com.somosayni.metricas.infrastructure.persistence.repository.JpaPostulacionSnapshotRepository;
import com.somosayni.metricas.infrastructure.persistence.repository.JpaRetoSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ObtenerMetricasEmpresaQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(ObtenerMetricasEmpresaQueryHandler.class);

    private final JpaRetoSnapshotRepository retoRepository;
    private final JpaPostulacionSnapshotRepository postulacionRepository;

    public ObtenerMetricasEmpresaQueryHandler(
            JpaRetoSnapshotRepository retoRepository,
            JpaPostulacionSnapshotRepository postulacionRepository) {
        this.retoRepository = retoRepository;
        this.postulacionRepository = postulacionRepository;
    }

    public MetricasEmpresa handle(ObtenerMetricasEmpresaQuery query) {
        try {
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
        } catch (InvalidDataAccessResourceUsageException ex) {
            log.warn("Tablas reto/postulacion no disponibles. Retornando metricas vacias para empresa {}", query.empresaId());
            return new MetricasEmpresa(query.empresaId(), 0, 0, 0, 0);
        }
    }
}
