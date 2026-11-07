package com.api.franquicia.model;

public record Franquicia(Long id, String nombre) {
    public Franquicia {
        nombre = new Nombre(nombre).valor();

    }
    public Franquicia cambiarNombre(String nuevoNombre) {
        return new Franquicia(id, nuevoNombre);
    }

}
