package com.api.franquicia.controller;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.controller.response.*;
import com.api.franquicia.dto.Requests;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import com.api.franquicia.model.Sucursal;
import com.api.franquicia.service.*;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {
    private final SucursalService service;
    private final ProductoService productos;
    @PostMapping("/crear")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Created>>> crear(@Valid @RequestBody Requests.Sucursal request) {
        return service.agregarSucursal(request.nombre(), request.franquicia().id()).map(value -> ApiResponses.created(value.id(), value.nombre(), "Sucursal creada correctamente"));
    }
    @GetMapping("/consultar/{id}")
    public Mono<ResponseEntity<ApiResponse<Sucursal>>> consultar(@PathVariable Long id) {
        return service.consultar(id).map(ApiResponses::ok);
    }
    @PutMapping("/actualizar-nombre/{id}")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Updated>>> actualizar(@PathVariable Long id,
            @Valid @RequestBody Requests.Nombre request) {
        return service.actualizarNombreSucursal(id, request.nombre()).map(value -> ApiResponses.updated(value.id(), value.nombre(), null, "Nombre de sucursal actualizado correctamente"));
    }
    @PostMapping("/{id}/productos/crear")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Created>>> agregarProducto(@PathVariable Long id,
            @Valid @RequestBody Requests.ProductoEnSucursal request) {
        return productos.agregarProducto(request.nombre(), request.stock(), id).map(value -> ApiResponses.created(value.id(), value.nombre(), "Producto creado correctamente"));
    }
}
