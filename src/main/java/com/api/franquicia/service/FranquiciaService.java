package com.api.franquicia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.franquicia.model.Franquicia;
import com.api.franquicia.repository.FranquiciaRepository;

import reactor.core.publisher.Mono;

@Service
public class FranquiciaService {
    
    @Autowired
    private FranquiciaRepository franquiciaRepository;

     public Mono<Franquicia> crearFranquicia(Franquicia franquicia) {
        return Mono.just(franquiciaRepository.save(franquicia));
    }

    public Mono<Void> actualizarFranquicia(Long id, String nuevoNombre) {
        return Mono.defer(() -> {
            Franquicia franquicia = franquiciaRepository.findById(id).orElseThrow();
            franquicia.setNombre(nuevoNombre);
            franquiciaRepository.save(franquicia);
            return Mono.empty(); 
        });
    }
    
    
}
