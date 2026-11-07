# Franquicia API

La persistencia se define como código en [terraform/](terraform/README.md), con MySQL DEV de Clever Cloud y un flujo para adoptar la base existente sin duplicarla. La CI valida Terraform sin modificar recursos de nube.

Para desplegar en Railway usando MySQL de Clever Cloud, consulta [la guía de despliegue](docs/RAILWAY.md). La configuración versionada está en `railway.json` y la integración continua en `.github/workflows/ci.yml`.

API REST con Java 17, Spring Boot 3.3.4, WebFlux, Reactor y MySQL mediante R2DBC.

## Ejecutar

Con Docker y Compose instalados:

```bash
docker compose up --build -d
docker compose logs -f api
```

API: `http://localhost:8080`. Compose espera a MySQL e inicializa las tablas. `docker compose down` detiene los servicios y conserva los datos en `db_data`. Puedes configurar `DB_PASSWORD` y `MYSQL_ROOT_PASSWORD` mediante el entorno o `.env` (ignorado por Git). Los valores predeterminados son solo para desarrollo.

Para ejecutar la API desde Maven o el IDE con la base Docker:

```bash
docker compose up -d db
mvn clean spring-boot:run
```

Requiere JDK 17 o superior y Maven. También puedes usar `./mvnw` en Linux/macOS o `.\mvnw.cmd` en Windows. El perfil predeterminado `local` conecta a `localhost:3307/franquicia_db` con usuario `franquicia`, contraseña `local-dev-password` e inicialización de tablas activada. En el IDE basta con ejecutar la clase principal sin variables adicionales.

En DBeaver usa el driver MySQL, host `localhost`, puerto `3307`, base `franquicia_db` y las mismas credenciales. El puerto 3307 evita conflictos con MySQL de Windows.

### Perfiles de configuración

| Perfil | Activación | Conexión |
|---|---|---|
| `local` | Predeterminado | MySQL Docker desde el equipo, puerto 3307 |
| `docker` | Automática en Compose | Host interno `db`, puerto 3306 |
| `cloud` | `SPRING_PROFILES_ACTIVE=cloud` | Variables obligatorias `DB_URL`, `DB_USER`, `DB_PASSWORD` |

La configuración compartida está en `application.properties`; las específicas en `application-local.properties`, `application-docker.properties` y `application-cloud.properties`. El perfil cloud prepara la configuración pero no despliega infraestructura ni crea tablas automáticamente.

Para seleccionar cloud desde Maven: `mvn spring-boot:run -Dspring-boot.run.profiles=cloud`, con las variables de conexión definidas en el entorno. `DB_URL` debe tener formato R2DBC, por ejemplo `r2dbc:mysql://servidor:3306/franquicia_db`. No guardes credenciales de nube en los archivos properties.

Las variables del entorno tienen prioridad: si exportaste `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_INIT_MODE` o `SPRING_PROFILES_ACTIVE` antes, elimínalas de la terminal/configuración del IDE para volver a los valores predeterminados. En Git Bash: `unset DB_URL DB_USER DB_PASSWORD DB_INIT_MODE SPRING_PROFILES_ACTIVE`. `SERVER_PORT` cambia el puerto de la API (8080 por defecto).

El script `schema.sql` sirve para bases nuevas. `CREATE TABLE IF NOT EXISTS` no migra tablas anteriores: respalda los datos y prepara una migración si ya existen. En cloud la inicialización está desactivada; utiliza migraciones versionadas. La ejecución local no requiere AWS.

## Contrato de respuestas

Todas las operaciones JSON usan `ApiResponse<T>`. `status` coincide con el estado HTTP.

| Operación | HTTP | data |
|---|---|---|
| Crear | 201 | `{"id":123,"nombre":"Centro","message":"Franquicia creada correctamente"}` |
| Actualizar | 200 | `{"update":true,"id":123,"nombre":"Nuevo nombre","message":"..."}` |
| Eliminar | 200 | `{"delete":true,"id":123,"nombre":"Producto","message":"Producto eliminado correctamente"}` |
| Consultar uno | 200 | Recurso |
| Consultar lista | 200 | Lista, posiblemente vacía |
| Error | Estado del error | `{"message":"Mensaje público"}` |

Ejemplo: `{"status":404,"data":{"message":"Producto no encontrado"}}`.

`ApiResponses` es una factoría explícita de `ResponseEntity<ApiResponse<T>>`: evita envolver dos veces y conserva objetos paginados completos como `data`. Los endpoints actuales no tienen paginación. No hay interceptor global que modifique archivos o respuestas vacías: usa `ResponseEntity<Resource>` o `ResponseEntity<Void>` directamente. Los estados 204/304 no admiten `ApiResponse` con cuerpo.

`@RestControllerAdvice` centraliza excepciones y otro manejador WebFlux cubre errores anteriores al controlador. Se contemplan 400, 401, 403, 404, 409, 500 y errores HTTP del framework, sin exponer trazas ni detalles internos.

No hay Spring Security ni autenticación en el proyecto. Se formatean 401/403 cuando se generan; esto no implementa permisos. Si añades Security, adapta su `ServerAuthenticationEntryPoint` y `ServerAccessDeniedHandler` al mismo contrato.

## Endpoints

| Método | Ruta | Cuerpo JSON |
|---|---|---|
| POST | `/api/franquicias/crear` | `{"nombre":"Centro"}` |
| GET | `/api/franquicias/consultar/{id}` | — |
| PUT | `/api/franquicias/actualizar-nombre/{id}` | `{"nombre":"Nuevo centro"}` |
| POST | `/api/franquicias/{id}/sucursales/crear` | `{"nombre":"Norte"}` |
| POST | `/api/sucursales/crear` | `{"nombre":"Norte","franquicia":{"id":1}}` |
| GET | `/api/sucursales/consultar/{id}` | — |
| PUT | `/api/sucursales/actualizar-nombre/{id}` | `{"nombre":"Nuevo norte"}` |
| POST | `/api/sucursales/{id}/productos/crear` | `{"nombre":"Café","stock":10}` |
| POST | `/api/productos/crear` | `{"nombre":"Café","stock":10,"sucursal":{"id":1}}` |
| GET | `/api/productos/consultar/{id}` | — |
| PUT | `/api/productos/actualizar-nombre/{id}` | `{"nombre":"Nuevo café"}` |
| PUT | `/api/productos/actualizar-stock/{id}` | `{"stock":20}` |
| DELETE | `/api/productos/eliminar/{id}` | — |
| GET | `/api/franquicias/consultar-mayor-stock/{id}` | — |

Las rutas anteriores sin acción (`POST /api/franquicias`, `GET /api/productos/{id}`, etc.) se reemplazaron por las rutas explícitas de esta tabla. Reinicia la API e importa de nuevo la colección de Postman. Las creaciones devuelven ID, nombre persistido y mensaje; las actualizaciones añaden `update:true` y el producto incluye su stock actual; las eliminaciones añaden `delete:true`. Las consultas devuelven el recurso o lista directamente en `data`.

Hay dos formas de crear una sucursal: `/api/sucursales/crear` recibe `franquicia.id` en JSON, mientras `/api/franquicias/{id}/sucursales/crear` toma ese ID de la URL y recibe solo el nombre. Para productos ocurre lo mismo: `/api/productos/crear` recibe `sucursal.id`, mientras `/api/sucursales/{id}/productos/crear` toma la sucursal de la URL. Son alternativas para la misma operación; no es necesario ejecutar ambas. Las dos validan el padre y usan el mismo servicio.

Los IDs los genera la base. Nombres obligatorios, máximo 150 caracteres; stock entero no negativo. Se comprueba la existencia del padre y se usan claves foráneas.

Mayor stock devuelve un producto por sucursal de la franquicia: `id`, `nombre`, `stock`, `sucursalId`, `sucursalNombre`. En empate gana el menor ID. Omite sucursales sin productos; devuelve `[]` para una franquicia existente sin productos y 404 si no existe. La selección se realiza en SQL con índices, sin cargar todos los productos.

Importa [la colección Postman](docs/Franquicia-api.postman_collection.json) y ejecuta la carpeta Flujo principal en orden: `base_url` apunta a localhost y los IDs se guardan automáticamente. Los [cURL completos](docs/CURL.md) incluyen rutas alternativas y errores.

## Verificación

```bash
mvn clean verify
```

También puedes usar `./mvnw clean verify` o `.\mvnw.cmd clean verify`. Las pruebas no requieren MySQL ni nube. Integración: WebTestClient y H2 mediante R2DBC en modo MySQL. Unitarias: Mockito y StepVerifier. Se cubren operaciones, errores, relaciones, mayor stock, desempate, archivos y respuestas vacías.

H2 no reemplaza la verificación contra MySQL real. En esta sesión pasaron 13 pruebas y se generó el JAR; `docker compose config --quiet` pasó, pero Docker Engine estaba detenido, por lo que no se verificaron los contenedores ni el driver contra MySQL.

## Arquitectura y patrones

- `model`: objetos inmutables y reglas de negocio (cambio de nombre y stock).
- `repository`: puertos reactivos de persistencia sin Spring Data.
- `service`: casos de uso sin dependencia de Spring.
- `infrastructure/persistence`: adaptadores R2DBC, filas y consultas SQL.
- `controller`, `dto`: HTTP, validación, respuestas y errores.
- `configuration`: composición e inyección por constructor.

Se aplica Repository con puertos y adaptadores y una factoría de respuestas. La orientación a objetos está en las invariantes y métodos del dominio; no se necesita herencia artificial. No hay `block()`, suscripción manual ni JDBC en producción.

La API no guarda estado de negocio en memoria y usa un pool limitado, permitiendo varias instancias con MySQL compartido. Las actualizaciones no incluyen versión optimista: si se necesitan garantías frente a ediciones concurrentes del mismo recurso, debe definirse y añadirse esa política. La consulta de mayor stock no está paginada.

La API y la base se despliegan por separado. Terraform describe MySQL DEV de Clever Cloud; su adopción autenticada debe completarse siguiendo terraform/README.md. Railway utiliza railway.json para configurar el despliegue.

Lombok `@RequiredArgsConstructor` genera los constructores de inyección de controladores, servicios y adaptadores; `@Slf4j` genera los loggers. Los records del dominio y contratos siguen siendo inmutables y conservan sus constructores de validación.
