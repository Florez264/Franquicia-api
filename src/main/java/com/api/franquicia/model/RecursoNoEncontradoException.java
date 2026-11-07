package com.api.franquicia.model;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso) {
        super(recurso + " no encontrado");
    }
}
