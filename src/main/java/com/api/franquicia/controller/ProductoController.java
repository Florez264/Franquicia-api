package com.api.franquicia.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.franquicia.model.Producto;
import com.api.franquicia.service.ProductoService;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> agregarProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.agregarProducto(producto);
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        try {
            String nuevoNombre = requestBody.get("nombre");
            productoService.actualizarNombreProducto(id, nuevoNombre);
            return new ResponseEntity<>("Nombre del producto actualizado exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar el producto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> actualizarStock(@PathVariable Long id, @RequestBody Map<String, Integer> requestBody) {
        try {
            Integer nuevoStock = requestBody.get("stock");
            
            Producto productoActualizado = productoService.modificarStock(id, nuevoStock);
            
            Map<String, Object> response = Collections.singletonMap("mensaje", "Stock actualizado exitosamente");
    
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
