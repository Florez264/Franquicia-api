package com.api.franquicia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.franquicia.model.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    
}
