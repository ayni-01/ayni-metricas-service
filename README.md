# 📊 Ayni Metricas Service

**Read Model de analítica para empresas.** Agrega datos de retos y postulaciones para ofrecer métricas del embudo de conversión sin interferir en la operación transaccional.

---

## Responsabilidad del Bounded Context

| | Descripción |
|---|---|
| **QUÉ HACE** | Calcula métricas agregadas de rendimiento para una empresa: retos activos, postulaciones recibidas, talentos aprobados y tasas de conversión. |
| **QUÉ NO HACE** | No crea ni modifica retos ni postulaciones. Es 100% de lectura — no posee estado transaccional propio. |

---

## Estrategia de datos: Snapshot JPA

Este servicio **no tiene dominio propio en base de datos**. En lugar de mantener tablas propias, lee directamente las tablas `reto` y `postulacion`, que son creadas y mantenidas por `retos-service` y `postulaciones-service` respectivamente.

Para lograr esto sin acoplamiento de código, se usan **entidades JPA "snapshot"**: clases anotadas con `@Entity` pero que mapean únicamente los campos necesarios para el cálculo de métricas, y que nunca escriben ni modifican datos. La configuración `ddl-auto: none` garantiza que este servicio no gestiona el esquema de la base de datos en ningún momento.

```
RetoSnapshot       → tabla `reto`        (campos: id, empresa_id, titulo, estado)
PostulacionSnapshot → tabla `postulacion` (campos: id, reto_id, estado)
```

---

## Arquitectura interna

```
infrastructure/rest
  └── MetricasController          ← Endpoints REST

application/query
  ├── ObtenerMetricasEmpresaQuery / QueryHandler
  └── ObtenerEmbudoQuery          / QueryHandler

domain/model
  ├── MetricasEmpresa             ← Record de respuesta de métricas generales
  └── EmbudoReto                  ← Record de respuesta por reto

infrastructure/persistence
  ├── entity/RetoSnapshot         ← Entidad JPA de solo lectura sobre tabla `reto`
  ├── entity/PostulacionSnapshot  ← Entidad JPA de solo lectura sobre tabla `postulacion`
  ├── repository/JpaRetoSnapshotRepository
  └── repository/JpaPostulacionSnapshotRepository
```

---

## Endpoints REST

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `GET` | `/api/v1/metricas/empresa/{empresaId}` | Métricas generales de la empresa | JWT requerido |
| `GET` | `/api/v1/metricas/embudo/{empresaId}` | Embudo de conversión por reto | JWT requerido |

### Respuesta: `MetricasEmpresa`

`GET /api/v1/metricas/empresa/{empresaId}`

```json
{
  "empresaId": "uuid",
  "retosActivos": 3,
  "nuevasPostulaciones": 24,
  "talentosEvaluados": 24,
  "talentosAprobados": 7
}
```

### Respuesta: `EmbudoReto`

`GET /api/v1/metricas/embudo/{empresaId}`

```json
[
  {
    "retoId": "uuid",
    "tituloReto": "API REST con Spring Boot",
    "postulados": 12,
    "evaluados": 8,
    "aprobados": 3,
    "tasaConversion": 25.0
  }
]
```

> `tasaConversion` se expresa como porcentaje (`aprobados / postulados * 100`).

---

## Cómo ejecutar

### Prerequisito importante

> **Este servicio debe iniciarse DESPUÉS de `retos-service` y `postulaciones-service`.**
>
> Como usa `ddl-auto: none` y lee sus tablas, si se inicia primero la base de datos puede no tener aún las tablas `reto` y `postulacion` creadas, lo que provocará un error de arranque en Hibernate.

### Local

```bash
mvn clean package -DskipTests
java -jar target/*.jar
```

El servicio quedará disponible en `http://localhost:8087`.

### Docker

```bash
cp .env.example .env   # Completar JWT_SECRET en el archivo .env
docker-compose up --build
```

---

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `JWT_SECRET` | Clave secreta para verificar tokens JWT (HS256) | `somosayni-jwt-secret-key-que-debe-ser-muy-larga-para-hs256-algoritmo-seguro` |
| `DB_USERNAME` | Usuario de la base de datos PostgreSQL | `somosayni` |
| `DB_PASSWORD` | Contraseña de la base de datos PostgreSQL | `somosayni123` |

---

## Puerto y documentación

| Recurso | URL |
|---------|-----|
| Servicio | `http://localhost:8087` |

## Swagger / OpenAPI

| | Link |
|---|---|
| **Swagger UI (local)** | [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html) |
| **OpenAPI JSON (local)** | [http://localhost:8087/api-docs](http://localhost:8087/api-docs) |
| **swagger.json (repo)** | [ver en GitHub](https://github.com/ayni-01/ayni-metricas-service/blob/main/swagger.json) |
| **Swagger Editor (online)** | [abrir en Swagger Editor](https://editor.swagger.io/?url=https://raw.githubusercontent.com/ayni-01/ayni-metricas-service/main/swagger.json) |

> Para probar los endpoints protegidos: copia el JWT del login → clic en **Authorize** → pega `Bearer <tu-token>`.

---

## Dependencias de datos

Este servicio comparte la base de datos PostgreSQL `somosayni` con los demás servicios del ecosistema Ayni. **No crea tablas propias.**

| Tabla | Propietario | Acceso |
|-------|------------|--------|
| `reto` | `retos-service` | Solo lectura (`RetoSnapshot`) |
| `postulacion` | `postulaciones-service` | Solo lectura (`PostulacionSnapshot`) |

---

## Stack tecnológico

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Spring Data JPA | (incluido en Boot) |
| Spring Security + JWT (jjwt) | 0.12.5 |
| PostgreSQL | 16 |
| SpringDoc OpenAPI (Swagger) | 2.5.0 |
| Lombok | (incluido en Boot) |
