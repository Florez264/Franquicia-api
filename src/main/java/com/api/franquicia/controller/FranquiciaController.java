package com.api.franquicia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.franquicia.model.Franquicia;
import com.api.franquicia.service.FranquiciaService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franquicias")
public class FranquiciaController {
    
    @Autowired
    private FranquiciaService franquiciaService;

    @PostMapping("/crear")
    public Mono<Franquicia> crearFranquicia(@RequestBody Franquicia franquicia) {
        return franquiciaService.crearFranquicia(franquicia);
    }

    @PutMapping("/actualizar/{id}")
     public Mono<ResponseEntity<String>> actualizarFranquicia(@PathVariable Long id, @RequestBody String nuevoNombre) {
         return franquiciaService.actualizarFranquicia(id, nuevoNombre)
             .then(Mono.just(new ResponseEntity<>("Franquicia actualizada exitosamente", HttpStatus.OK)))
             .onErrorResume(e -> Mono.just(new ResponseEntity<>("Error al actualizar la franquicia", HttpStatus.INTERNAL_SERVER_ERROR)));
     }

}
