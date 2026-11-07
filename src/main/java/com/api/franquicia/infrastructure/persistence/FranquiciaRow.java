package com.api.franquicia.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import com.api.franquicia.model.Franquicia;

@Table("franquicia")
record FranquiciaRow(@Id Long id, String nombre) {
    static FranquiciaRow from(Franquicia value) { return new FranquiciaRow(value.id(), value.nombre()); }
    Franquicia toDomain() { return new Franquicia(id, nombre); }
}
