package com.api.franquicia.model;

public record Nombre(String valor) {
    public Nombre {
        if (valor == null || valor.isBlank() || valor.length() > 150) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener máximo 150 caracteres");
        }
        valor = valor.trim();
    }
}
