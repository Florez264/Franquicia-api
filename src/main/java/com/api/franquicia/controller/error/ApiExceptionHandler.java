package com.api.franquicia.controller.error;

import com.api.franquicia.controller.response.*;
import com.api.franquicia.model.RecursoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<ApiResponses.Error>> notFound(RecursoNoEncontradoException ex) {
        return ApiResponses.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ApiResponses.Error>> invalid(IllegalArgumentException ex) {
        return ApiResponses.error(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
    }
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ApiResponse<ApiResponses.Error>> conflict(Exception ex) {
        return ApiResponses.error(HttpStatus.CONFLICT, "La operación entra en conflicto con los datos existentes");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiResponses.Error>> internal(Exception ex) {
        log.error("Error no controlado procesando la petición", ex);
        return ApiResponses.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
    @Override
    protected Mono<ResponseEntity<Object>> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        if (exchange.getResponse().isCommitted()) return Mono.error(ex);
        return Mono.just(new ResponseEntity<>(new ApiResponse<>(status.value(),
            new ApiResponses.Error(ErrorMessages.forStatus(status.value()))), headers, status));
    }
}
