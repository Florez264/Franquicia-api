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

import com.api.franquicia.model.Sucursal;
import com.api.franquicia.service.SucursalService;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @PostMapping
    public ResponseEntity<Sucursal> agregarSucursal(@RequestBody Sucursal nuevaSucursal) {
        try {
            Sucursal sucursalCreada = sucursalService.agregarSucursal(nuevaSucursal);
            return new ResponseEntity<>(sucursalCreada, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarSucursal(@PathVariable Long id, @RequestBody Sucursal nuevoDatos) {
        try {
            sucursalService.actualizarNombreSucursal(id, nuevoDatos.getNombre());
            return new ResponseEntity<>("Nombre de sucursal actualizado exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar la sucursal", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    

}
