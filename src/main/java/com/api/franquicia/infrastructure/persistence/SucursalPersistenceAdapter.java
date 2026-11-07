package com.api.franquicia.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.model.*;
import com.api.franquicia.repository.SucursalRepository;
import org.springframework.stereotype.Repository;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class SucursalPersistenceAdapter implements SucursalRepository {
    private final SucursalCrudRepository crud;

    public Mono<Sucursal> findById(Long id) { return crud.findById(id).map(SucursalRow::toDomain); }
    public Mono<Sucursal> save(Sucursal recurso) {
        return crud.save(SucursalRow.from(recurso)).map(SucursalRow::toDomain);
    }

}
