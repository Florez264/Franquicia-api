package com.api.franquicia.controller.error;

import com.api.franquicia.controller.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

/** Cubre fallos anteriores al controlador, por ejemplo rutas inexistentes o filtros. */
@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class ApiErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper mapper;
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable error) {
        if (exchange.getResponse().isCommitted()) return Mono.error(error);
        HttpStatusCode status = error instanceof ResponseStatusException ex
            ? ex.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR;
        if (status.is5xxServerError()) log.error("Error fuera del controlador", error);
        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(new ApiResponse<>(status.value(),
                new ApiResponses.Error(ErrorMessages.forStatus(status.value()))));
        } catch (JsonProcessingException ex) { return Mono.error(ex); }
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (exchange.getRequest().getMethod() == HttpMethod.HEAD) return response.setComplete();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
