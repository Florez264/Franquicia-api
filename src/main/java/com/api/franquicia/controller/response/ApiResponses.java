package com.api.franquicia.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/** Factoría explícita: nunca intercepta archivos, respuestas vacías ni respuestas ya envueltas. */
public final class ApiResponses {
    private ApiResponses() {}
    public record Created(Long id, String nombre, String message) {}
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Updated(boolean update, Long id, String nombre, Integer stock, String message) {}
    public record Deleted(boolean delete, Long id, String nombre, String message) {}
    public record Error(String message) {}

    public static <T> ResponseEntity<ApiResponse<T>> of(HttpStatusCode status, T data) {
        if (data instanceof ApiResponse<?>) throw new IllegalArgumentException("La respuesta ya está envuelta");
        if (status.value() == 204 || status.value() == 304 || status.is1xxInformational()) {
            throw new IllegalArgumentException("Este código HTTP no admite cuerpo; use ResponseEntity sin ApiResponse");
        }
        return ResponseEntity.status(status).body(new ApiResponse<>(status.value(), data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) { return of(HttpStatus.OK, data); }
    public static ResponseEntity<ApiResponse<Created>> created(Long id, String nombre, String message) {
        return of(HttpStatus.CREATED, new Created(id, nombre, message));
    }
    public static ResponseEntity<ApiResponse<Updated>> updated(Long id, String nombre, Integer stock, String message) {
        return ok(new Updated(true, id, nombre, stock, message));
    }
    public static ResponseEntity<ApiResponse<Deleted>> deleted(Long id, String nombre) {
        return ok(new Deleted(true, id, nombre, "Producto eliminado correctamente"));
    }
    public static ResponseEntity<ApiResponse<Error>> error(HttpStatusCode status, String message) {
        return of(status, new Error(message));
    }
}
