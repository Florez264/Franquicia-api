package com.api.franquicia.service;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.model.*;
import com.api.franquicia.repository.SucursalRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SucursalService {
    private final SucursalRepository repository;
    private final FranquiciaService franquicias;
    public Mono<Sucursal> agregarSucursal(String nombre, Long franquiciaId) {
        return franquicias.consultar(franquiciaId)
            .flatMap(parent -> repository.save(new Sucursal(null, nombre, parent.id())));
    }
    public Mono<Sucursal> consultar(Long id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new RecursoNoEncontradoException("Sucursal")));
    }
    public Mono<Sucursal> actualizarNombreSucursal(Long id, String nombre) {
        return consultar(id).flatMap(value -> repository.save(value.cambiarNombre(nombre)));
    }
}
