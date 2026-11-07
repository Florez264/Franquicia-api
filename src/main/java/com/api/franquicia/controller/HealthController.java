package com.api.franquicia.controller;

import com.api.franquicia.controller.response.ApiResponse;
import com.api.franquicia.controller.response.ApiResponses;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** Readiness: consulta liviana sin exponer datos ni detalles de conexión. */
@RestController
@RequiredArgsConstructor
public class HealthController {
    private final DatabaseClient database;

    @GetMapping("/api/health")
    public Mono<ResponseEntity<ApiResponse<Map<String, String>>>> health() {
        return database.sql("SELECT 1").fetch().first()
            .switchIfEmpty(Mono.error(new IllegalStateException("Empty health query")))
            .timeout(Duration.ofSeconds(5))
            .map(result -> ApiResponses.ok(Map.of("state", "UP")))
            .onErrorResume(error -> Mono.just(ApiResponses.of(HttpStatus.SERVICE_UNAVAILABLE,
                Map.of("state", "DOWN"))));
    }
}
