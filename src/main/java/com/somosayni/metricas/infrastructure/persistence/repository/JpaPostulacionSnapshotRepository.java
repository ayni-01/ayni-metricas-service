package com.somosayni.metricas.infrastructure.persistence.repository;

import com.somosayni.metricas.infrastructure.persistence.entity.PostulacionSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaPostulacionSnapshotRepository extends JpaRepository<PostulacionSnapshot, String> {
    List<PostulacionSnapshot> findByRetoId(String retoId);
    long countByRetoIdAndEstado(String retoId, PostulacionSnapshot.EstadoPostulacion estado);
}
