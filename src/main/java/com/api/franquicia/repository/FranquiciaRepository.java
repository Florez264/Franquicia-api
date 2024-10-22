package com.api.franquicia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.franquicia.model.Franquicia;

public interface FranquiciaRepository extends JpaRepository<Franquicia, Long> {
    
}
