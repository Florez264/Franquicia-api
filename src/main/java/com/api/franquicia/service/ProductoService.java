package com.api.franquicia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.franquicia.model.Producto;
import com.api.franquicia.repository.ProductoRepository;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    public Producto agregarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto modificarStock(Long id, int nuevoStock) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    public Producto actualizarNombreProducto(Long id, String nuevoNombre) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setNombre(nuevoNombre);
        return productoRepository.save(producto);
    }
}
