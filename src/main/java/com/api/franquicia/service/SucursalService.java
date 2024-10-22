package com.api.franquicia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.franquicia.model.Sucursal;
import com.api.franquicia.repository.SucursalRepository;

@Service
public class SucursalService {
    
    @Autowired
    private SucursalRepository sucursalRepository;

    public Sucursal agregarSucursal(Sucursal sucursal) {
        return sucursalRepository.save(sucursal);
    }

    public Sucursal actualizarNombreSucursal(Long id, String nuevoNombre) {
        Sucursal sucursal = sucursalRepository.findById(id).orElseThrow();
        sucursal.setNombre(nuevoNombre);
        return sucursalRepository.save(sucursal);
    }
    
}
