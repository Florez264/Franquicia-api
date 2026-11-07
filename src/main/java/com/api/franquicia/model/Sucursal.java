package com.api.franquicia.model;

public record Sucursal(Long id, String nombre, Long franquiciaId) {
    public Sucursal {
        nombre = new Nombre(nombre).valor();
        if (franquiciaId == null || franquiciaId <= 0) throw new IllegalArgumentException("La relación padre es obligatoria");
    }
    public Sucursal cambiarNombre(String nuevoNombre) {
        return new Sucursal(id, nuevoNombre, franquiciaId);
    }

}
