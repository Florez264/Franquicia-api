package com.api.franquicia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public final class Requests {
    private Requests() {}
    public record Nombre(@NotBlank @Size(max = 150) String nombre) {}
    public record Referencia(@NotNull @Positive Long id) {}
    public record Sucursal(@NotBlank @Size(max = 150) String nombre, @NotNull @Valid Referencia franquicia) {}
    public record Producto(@NotBlank @Size(max = 150) String nombre, @NotNull @Min(0) Integer stock,
                           @NotNull @Valid Referencia sucursal) {}
    public record ProductoEnSucursal(@NotBlank @Size(max = 150) String nombre, @NotNull @Min(0) Integer stock) {}
    public record Stock(@NotNull @Min(0) Integer stock) {}
}
