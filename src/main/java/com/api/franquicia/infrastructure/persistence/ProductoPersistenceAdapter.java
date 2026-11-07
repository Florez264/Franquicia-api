package com.api.franquicia.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.model.*;
import com.api.franquicia.repository.ProductoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class ProductoPersistenceAdapter implements ProductoRepository {
    private final ProductoCrudRepository crud;
    private final DatabaseClient client;
    public Mono<Producto> findById(Long id) { return crud.findById(id).map(ProductoRow::toDomain); }
    public Mono<Producto> save(Producto recurso) {
        return crud.save(ProductoRow.from(recurso)).map(ProductoRow::toDomain);
    }

    public Mono<Void> deleteById(Long id) { return crud.deleteById(id); }

    // Un ganador por sucursal; en empate gana el ID menor. No carga todos los productos en memoria.
    public Flux<ProductoMayorStock> mayoresPorSucursal(Long franquiciaId) {
        return client.sql("""
            SELECT p.id, p.nombre, p.stock, s.id AS sucursal_id, s.nombre AS sucursal_nombre
            FROM producto p JOIN sucursal s ON s.id = p.sucursal_id
            WHERE s.franquicia_id = :franquiciaId
              AND NOT EXISTS (
                SELECT 1 FROM producto otro WHERE otro.sucursal_id = p.sucursal_id
                  AND (otro.stock > p.stock OR (otro.stock = p.stock AND otro.id < p.id))
              )
            ORDER BY s.id
            """).bind("franquiciaId", franquiciaId)
            .map((row, metadata) -> new ProductoMayorStock(row.get("id", Long.class),
                row.get("nombre", String.class), row.get("stock", Integer.class),
                row.get("sucursal_id", Long.class), row.get("sucursal_nombre", String.class)))
            .all();
    }

}
