package com.api.franquicia.dto;

public class FranquiciaDto {
    private Long id;
    private String nombre;

    public FranquiciaDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}