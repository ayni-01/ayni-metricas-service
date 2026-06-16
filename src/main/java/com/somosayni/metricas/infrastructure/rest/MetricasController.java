package com.somosayni.metricas.infrastructure.rest;

import com.somosayni.metricas.application.query.*;
import com.somosayni.metricas.domain.model.EmbudoReto;
import com.somosayni.metricas.domain.model.MetricasEmpresa;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metricas")
public class MetricasController {

    private final ObtenerMetricasEmpresaQueryHandler metricasHandler;
    private final ObtenerEmbudoQueryHandler embudoHandler;

    public MetricasController(
            ObtenerMetricasEmpresaQueryHandler metricasHandler,
            ObtenerEmbudoQueryHandler embudoHandler) {
        this.metricasHandler = metricasHandler;
        this.embudoHandler = embudoHandler;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<MetricasEmpresa> obtenerMetricas(@PathVariable String empresaId) {
        return ResponseEntity.ok(metricasHandler.handle(new ObtenerMetricasEmpresaQuery(empresaId)));
    }

    @GetMapping("/embudo/{empresaId}")
    public ResponseEntity<List<EmbudoReto>> obtenerEmbudo(@PathVariable String empresaId) {
        return ResponseEntity.ok(embudoHandler.handle(new ObtenerEmbudoQuery(empresaId)));
    }
}
