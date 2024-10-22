package com.api.franquicia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.franquicia.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
}
