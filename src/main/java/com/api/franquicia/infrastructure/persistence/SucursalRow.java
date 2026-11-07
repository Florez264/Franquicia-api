package com.api.franquicia.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import com.api.franquicia.model.Sucursal;

@Table("sucursal")
record SucursalRow(@Id Long id, String nombre, Long franquiciaId) {
    static SucursalRow from(Sucursal value) { return new SucursalRow(value.id(), value.nombre(), value.franquiciaId()); }
    Sucursal toDomain() { return new Sucursal(id, nombre, franquiciaId()); }
}
