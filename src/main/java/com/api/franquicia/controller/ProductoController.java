package com.api.franquicia.controller;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.controller.response.*;
import com.api.franquicia.dto.Requests;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import com.api.franquicia.model.Producto;
import com.api.franquicia.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService service;
    @PostMapping("/crear")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Created>>> crear(@Valid @RequestBody Requests.Producto request) {
        return service.agregarProducto(request.nombre(), request.stock(), request.sucursal().id())
            .map(value -> ApiResponses.created(value.id(), value.nombre(), "Producto creado correctamente"));
    }
    @GetMapping("/consultar/{id}")
    public Mono<ResponseEntity<ApiResponse<Producto>>> consultar(@PathVariable Long id) {
        return service.consultar(id).map(ApiResponses::ok);
    }
    @PutMapping("/actualizar-nombre/{id}")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Updated>>> actualizar(@PathVariable Long id,
            @Valid @RequestBody Requests.Nombre request) {
        return service.actualizarNombreProducto(id, request.nombre()).map(value -> ApiResponses.updated(value.id(), value.nombre(), value.stock(), "Nombre de producto actualizado correctamente"));
    }
    @PutMapping("/actualizar-stock/{id}")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Updated>>> stock(@PathVariable Long id,
            @Valid @RequestBody Requests.Stock request) {
        return service.modificarStock(id, request.stock()).map(value -> ApiResponses.updated(value.id(), value.nombre(), value.stock(), "Stock de producto actualizado correctamente"));
    }
    @DeleteMapping("/eliminar/{id}")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Deleted>>> eliminar(@PathVariable Long id) {
        return service.eliminarProducto(id).map(value -> ApiResponses.deleted(value.id(), value.nombre()));
    }
}
