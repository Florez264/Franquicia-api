package com.api.franquicia.dto;

import com.api.franquicia.model.Franquicia;
import com.api.franquicia.model.Producto;
import com.api.franquicia.model.Sucursal;

public class ProductoDto {
    private Long id;
    private String nombre;
    private int stock;
    private Sucursal sucursal;
    private Franquicia franquicia;

    public ProductoDto(Producto producto, Sucursal sucursal, Franquicia franquicia) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.stock = producto.getStock();
        this.sucursal = sucursal;
        this.franquicia = franquicia;
    }
}
