package com.api.franquicia.dto;

public class SucursalDto {
    
    private Long id;
    private String nombre;
    private FranquiciaDto franchisa;

    // Constructor
    public SucursalDto() {}

    // Getters y setters
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

    public FranquiciaDto getFranchisa() {
        return franchisa;
    }

    public void setFranchisa(FranquiciaDto franchisa) {
        this.franchisa = franchisa;
    }
}
