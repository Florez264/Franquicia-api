package com.api.franquicia.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.model.*;
import com.api.franquicia.repository.FranquiciaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class FranquiciaPersistenceAdapter implements FranquiciaRepository {
    private final FranquiciaCrudRepository crud;

    public Mono<Franquicia> findById(Long id) { return crud.findById(id).map(FranquiciaRow::toDomain); }
    public Mono<Franquicia> save(Franquicia recurso) {
        return crud.save(FranquiciaRow.from(recurso)).map(FranquiciaRow::toDomain);
    }

}
