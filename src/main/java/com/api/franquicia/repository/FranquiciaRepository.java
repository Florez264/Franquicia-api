package com.api.franquicia.repository;

import com.api.franquicia.model.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/** Puerto de persistencia: no depende de Spring Data ni de MySQL. */
public interface FranquiciaRepository {
    Mono<Franquicia> findById(Long id);
    Mono<Franquicia> save(Franquicia recurso);

}
