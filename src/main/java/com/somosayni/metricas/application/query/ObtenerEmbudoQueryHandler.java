package com.somosayni.metricas.application.query;

import com.somosayni.metricas.domain.model.EmbudoReto;
import com.somosayni.metricas.infrastructure.persistence.entity.PostulacionSnapshot;
import com.somosayni.metricas.infrastructure.persistence.entity.RetoSnapshot;
import com.somosayni.metricas.infrastructure.persistence.repository.JpaPostulacionSnapshotRepository;
import com.somosayni.metricas.infrastructure.persistence.repository.JpaRetoSnapshotRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ObtenerEmbudoQueryHandler {

    private final JpaRetoSnapshotRepository retoRepository;
    private final JpaPostulacionSnapshotRepository postulacionRepository;

    public ObtenerEmbudoQueryHandler(
            JpaRetoSnapshotRepository retoRepository,
            JpaPostulacionSnapshotRepository postulacionRepository) {
        this.retoRepository = retoRepository;
        this.postulacionRepository = postulacionRepository;
    }

    public List<EmbudoReto> handle(ObtenerEmbudoQuery query) {
        return retoRepository
                .findByEmpresaIdAndEstado(query.empresaId(), RetoSnapshot.EstadoReto.ACTIVO)
                .stream()
                .map(reto -> {
                    List<PostulacionSnapshot> postulaciones = postulacionRepository.findByRetoId(reto.getId());
                    int postulados = postulaciones.size();
                    int evaluados = (int) postulaciones.stream()
                            .filter(p -> p.getEstado() != PostulacionSnapshot.EstadoPostulacion.EN_REVISION)
                            .count();
                    int aprobados = (int) postulaciones.stream()
                            .filter(p -> p.getEstado() == PostulacionSnapshot.EstadoPostulacion.APROBADO)
                            .count();
                    double tasa = postulados > 0 ? (double) aprobados / postulados * 100 : 0;
                    return new EmbudoReto(reto.getId(), reto.getTitulo(), postulados, evaluados, aprobados, tasa);
                })
                .toList();
    }
}
