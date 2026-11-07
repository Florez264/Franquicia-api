package com.api.franquicia.controller.error;

final class ErrorMessages {
    private ErrorMessages() {}
    static String forStatus(int status) {
        return switch (status) {
            case 400 -> "Datos de entrada inválidos";
            case 401 -> "Autenticación requerida";
            case 403 -> "Acceso denegado";
            case 404 -> "Recurso no encontrado";
            case 405 -> "Método HTTP no permitido";
            case 406 -> "Formato de respuesta no aceptable";
            case 409 -> "Conflicto con los datos existentes";
            case 415 -> "Tipo de contenido no soportado";
            default -> status >= 500 ? "Error interno del servidor" : "No se pudo procesar la petición";
        };
    }
}
