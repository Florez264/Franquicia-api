# cURL para Postman

Reinicia la API con `mvn clean spring-boot:run`. Importa cada comando mediante **Import → Raw text**, o importa `Franquicia-api.postman_collection.json` de esta carpeta para guardar automáticamente los IDs.

Los comandos usan IDs de ejemplo: franquicia 1, sucursal 1, primer producto 1 y segundo producto 2. Sustitúyelos por los devueltos en `data.id`. La base puede contener datos anteriores. Cada POST crea un recurso nuevo.

## Flujo principal

### 01 Crear franquicia

```bash
curl --request POST 'http://localhost:8080/api/franquicias/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Franquicia Centro"}'
```

### 02 Crear sucursal

```bash
curl --request POST 'http://localhost:8080/api/sucursales/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Sucursal Norte","franquicia":{"id":1}}'
```

### 03 Crear producto

```bash
curl --request POST 'http://localhost:8080/api/productos/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Cafe","stock":10,"sucursal":{"id":1}}'
```

### 04 Crear segundo producto

```bash
curl --request POST 'http://localhost:8080/api/sucursales/1/productos/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Pan","stock":20}'
```

### 05 Consultar franquicia

```bash
curl --request GET 'http://localhost:8080/api/franquicias/consultar/1'
```

### 06 Consultar sucursal

```bash
curl --request GET 'http://localhost:8080/api/sucursales/consultar/1'
```

### 07 Consultar producto

```bash
curl --request GET 'http://localhost:8080/api/productos/consultar/1'
```

### 08 Mayor stock por sucursal

```bash
curl --request GET 'http://localhost:8080/api/franquicias/consultar-mayor-stock/1'
```

### 09 Cambiar nombre de franquicia

```bash
curl --request PUT 'http://localhost:8080/api/franquicias/actualizar-nombre/1' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Franquicia Actualizada"}'
```

### 10 Cambiar nombre de sucursal

```bash
curl --request PUT 'http://localhost:8080/api/sucursales/actualizar-nombre/1' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Sucursal Actualizada"}'
```

### 11 Cambiar nombre de producto

```bash
curl --request PUT 'http://localhost:8080/api/productos/actualizar-nombre/1' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Cafe Especial"}'
```

### 12 Cambiar stock

```bash
curl --request PUT 'http://localhost:8080/api/productos/actualizar-stock/1' \
  --header 'Content-Type: application/json' \
  --data '{"stock":30}'
```

### 13 Mayor stock actualizado

```bash
curl --request GET 'http://localhost:8080/api/franquicias/consultar-mayor-stock/1'
```

### 14 Eliminar segundo producto

```bash
curl --request DELETE 'http://localhost:8080/api/productos/eliminar/2'
```

### 15 Consultar eliminado (404)

```bash
curl --request GET 'http://localhost:8080/api/productos/consultar/2'
```

## Alternativas de creación

El padre va en la URL en lugar del JSON. Usan los mismos servicios y validaciones que `/api/sucursales/crear` y `/api/productos/crear`. No ejecutan una operación adicional sobre el padre. Ejecutar ambas variantes crea dos recursos.

### Crear sucursal con padre en URL

```bash
curl --request POST 'http://localhost:8080/api/franquicias/1/sucursales/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Sucursal Sur"}'
```

### Crear producto con padre en URL

```bash
curl --request POST 'http://localhost:8080/api/sucursales/1/productos/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Producto Alternativo","stock":5}'
```

## Errores de ejemplo

### Nombre vacío

```bash
curl --request POST 'http://localhost:8080/api/franquicias/crear' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":""}'
```

### Stock negativo

```bash
curl --request PUT 'http://localhost:8080/api/productos/actualizar-stock/1' \
  --header 'Content-Type: application/json' \
  --data '{"stock":-1}'
```

### Producto inexistente

```bash
curl --request GET 'http://localhost:8080/api/productos/consultar/999999'
```

