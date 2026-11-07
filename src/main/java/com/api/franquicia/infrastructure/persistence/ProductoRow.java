package com.api.franquicia.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import com.api.franquicia.model.Producto;

@Table("producto")
record ProductoRow(@Id Long id, String nombre, int stock, Long sucursalId) {
    static ProductoRow from(Producto value) { return new ProductoRow(value.id(), value.nombre(), value.stock(), value.sucursalId()); }
    Producto toDomain() { return new Producto(id, nombre, stock(), sucursalId()); }
}
