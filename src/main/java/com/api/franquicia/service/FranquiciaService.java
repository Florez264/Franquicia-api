package com.api.franquicia.service;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.model.*;
import com.api.franquicia.repository.FranquiciaRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranquiciaService {
    private final FranquiciaRepository repository;

    public Mono<Franquicia> crearFranquicia(String nombre) {
        return Mono.defer(() -> repository.save(new Franquicia(null, nombre)));
    }
    public Mono<Franquicia> consultar(Long id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new RecursoNoEncontradoException("Franquicia")));
    }
    public Mono<Franquicia> actualizarFranquicia(Long id, String nombre) {
        return consultar(id).flatMap(value -> repository.save(value.cambiarNombre(nombre)));
    }
}
