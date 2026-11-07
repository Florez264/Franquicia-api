package com.api.franquicia.controller;

import lombok.RequiredArgsConstructor;
import com.api.franquicia.controller.response.*;
import com.api.franquicia.dto.Requests;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import com.api.franquicia.model.Franquicia;
import com.api.franquicia.model.ProductoMayorStock;
import com.api.franquicia.service.*;
import java.util.List;

@RestController
@RequestMapping("/api/franquicias")
@RequiredArgsConstructor
public class FranquiciaController {
    private final FranquiciaService service;
    private final ProductoService productos;
    private final SucursalService sucursales;
    @PostMapping("/crear")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Created>>> crear(@Valid @RequestBody Requests.Nombre request) {
        return service.crearFranquicia(request.nombre()).map(value -> ApiResponses.created(value.id(), value.nombre(), "Franquicia creada correctamente"));
    }
    @GetMapping("/consultar/{id}")
    public Mono<ResponseEntity<ApiResponse<Franquicia>>> consultar(@PathVariable Long id) {
        return service.consultar(id).map(ApiResponses::ok);
    }
    @PutMapping("/actualizar-nombre/{id}")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Updated>>> actualizar(@PathVariable Long id,
            @Valid @RequestBody Requests.Nombre request) {
        return service.actualizarFranquicia(id, request.nombre()).map(value -> ApiResponses.updated(value.id(), value.nombre(), null, "Nombre de franquicia actualizado correctamente"));
    }
    @PostMapping("/{id}/sucursales/crear")
    public Mono<ResponseEntity<ApiResponse<ApiResponses.Created>>> agregarSucursal(@PathVariable Long id,
            @Valid @RequestBody Requests.Nombre request) {
        return sucursales.agregarSucursal(request.nombre(), id).map(value -> ApiResponses.created(value.id(), value.nombre(), "Sucursal creada correctamente"));
    }
    @GetMapping("/consultar-mayor-stock/{id}")
    public Mono<ResponseEntity<ApiResponse<List<ProductoMayorStock>>>> mayores(@PathVariable Long id) {
        return productos.mayoresPorSucursal(id).collectList().map(ApiResponses::ok);
    }
}
