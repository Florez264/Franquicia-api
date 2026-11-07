package com.api.franquicia.repository;

import com.api.franquicia.model.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/** Puerto de persistencia: no depende de Spring Data ni de MySQL. */
public interface ProductoRepository {
    Mono<Producto> findById(Long id);
    Mono<Producto> save(Producto recurso);
    Mono<Void> deleteById(Long id);
    Flux<ProductoMayorStock> mayoresPorSucursal(Long franquiciaId);
}
