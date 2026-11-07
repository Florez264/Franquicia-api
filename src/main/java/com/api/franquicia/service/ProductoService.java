package com.api.franquicia.service;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.model.*;
import com.api.franquicia.repository.ProductoRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repository;
    private final SucursalService sucursales;
    private final FranquiciaService franquicias;
    public Mono<Producto> agregarProducto(String nombre, int stock, Long sucursalId) {
        return sucursales.consultar(sucursalId)
            .flatMap(parent -> repository.save(new Producto(null, nombre, stock, parent.id())));
    }
    public Mono<Producto> consultar(Long id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new RecursoNoEncontradoException("Producto")));
    }
    public Mono<Producto> eliminarProducto(Long id) {
        return consultar(id).flatMap(value -> repository.deleteById(value.id()).thenReturn(value));
    }
    public Mono<Producto> modificarStock(Long id, int stock) {
        return consultar(id).flatMap(value -> repository.save(value.cambiarStock(stock)));
    }
    public Mono<Producto> actualizarNombreProducto(Long id, String nombre) {
        return consultar(id).flatMap(value -> repository.save(value.cambiarNombre(nombre)));
    }
    public Flux<ProductoMayorStock> mayoresPorSucursal(Long franquiciaId) {
        return franquicias.consultar(franquiciaId).flatMapMany(value -> repository.mayoresPorSucursal(value.id()));
    }
}
