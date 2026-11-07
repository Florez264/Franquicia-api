package com.api.franquicia.model;

public record Producto(Long id, String nombre, int stock, Long sucursalId) {
    public Producto {
        nombre = new Nombre(nombre).valor();
        if (sucursalId == null || sucursalId <= 0) throw new IllegalArgumentException("La relación padre es obligatoria");
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
    }
    public Producto cambiarNombre(String nuevoNombre) {
        return new Producto(id, nuevoNombre, stock, sucursalId);
    }

    public Producto cambiarStock(int nuevoStock) {
        return new Producto(id, nombre, nuevoStock, sucursalId);
    }

}
