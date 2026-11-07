package com.api.franquicia;

import com.api.franquicia.controller.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(ApiIntegrationTest.TestEndpoints.class)
class ApiIntegrationTest {
    @Autowired WebTestClient client;
    @Autowired DatabaseClient db;

    @Test
    void healthChecksDatabaseWithoutExposingConnectionDetails() {
        client.get().uri("/api/health").exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.data.state").isEqualTo("UP")
            .jsonPath("$.data.password").doesNotExist();
    }

    @BeforeEach
    void clean() {
        db.sql("DELETE FROM producto").fetch().rowsUpdated()
            .then(db.sql("DELETE FROM sucursal").fetch().rowsUpdated())
            .then(db.sql("DELETE FROM franquicia").fetch().rowsUpdated()).block();
    }

    long create(String path, Object body) {
        byte[] bytes = client.post().uri(path).bodyValue(body).exchange()
            .expectStatus().isCreated().expectBody()
            .jsonPath("$.status").isEqualTo(201)
            .jsonPath("$.data.id").isNumber()
            .jsonPath("$.data.nombre").isEqualTo(((Map<?, ?>) body).get("nombre"))
            .jsonPath("$.data.message").isNotEmpty()
            .returnResult().getResponseBody();
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readTree(bytes).at("/data/id").asLong();
        } catch (Exception ex) { throw new AssertionError(ex); }
    }

    @Test
    void completeWorkflowAndMaximumStockByBranch() {
        long franchise = create("/api/franquicias/crear", Map.of("nombre", "Centro"));
        long branch = create("/api/sucursales/crear", Map.of("nombre", "Norte", "franquicia", Map.of("id", franchise)));
        long south = create("/api/franquicias/" + franchise + "/sucursales/crear", Map.of("nombre", "Sur"));
        create("/api/franquicias/" + franchise + "/sucursales/crear", Map.of("nombre", "Sin productos"));
        long a = create("/api/productos/crear", Map.of("nombre", "A", "stock", 5, "sucursal", Map.of("id", branch)));
        long winner = create("/api/sucursales/" + branch + "/productos/crear", Map.of("nombre", "B", "stock", 10));
        create("/api/sucursales/" + branch + "/productos/crear", Map.of("nombre", "Empate", "stock", 10));
        long southProduct = create("/api/sucursales/" + south + "/productos/crear", Map.of("nombre", "C", "stock", 2));
        long other = create("/api/franquicias/crear", Map.of("nombre", "Otra"));
        long otherBranch = create("/api/franquicias/" + other + "/sucursales/crear", Map.of("nombre", "Otra"));
        create("/api/sucursales/" + otherBranch + "/productos/crear", Map.of("nombre", "Excluido", "stock", 999));

        client.get().uri("/api/franquicias/consultar-mayor-stock/" + franchise).exchange()
            .expectStatus().isOk().expectBody().jsonPath("$.status").isEqualTo(200)
            .jsonPath("$.data.length()").isEqualTo(2)
            .jsonPath("$.data[0].id").isEqualTo(winner)
            .jsonPath("$.data[0].sucursalId").isEqualTo(branch)
            .jsonPath("$.data[0].sucursalNombre").isEqualTo("Norte")
            .jsonPath("$.data[1].id").isEqualTo(southProduct);

        update("/api/franquicias/actualizar-nombre/" + franchise, Map.of("nombre", "Nuevo centro"));
        update("/api/sucursales/actualizar-nombre/" + branch, Map.of("nombre", "Nuevo norte"));
        update("/api/productos/actualizar-nombre/" + a, Map.of("nombre", "Nuevo A"));
        update("/api/productos/actualizar-stock/" + a, Map.of("stock", 20));
        client.get().uri("/api/productos/consultar/" + a).exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.status").isEqualTo(200).jsonPath("$.data.stock").isEqualTo(20)
            .jsonPath("$.data.nombre").isEqualTo("Nuevo A");
        client.get().uri("/api/franquicias/consultar/" + franchise).exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.data.nombre").isEqualTo("Nuevo centro");
        client.get().uri("/api/sucursales/consultar/" + branch).exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.data.nombre").isEqualTo("Nuevo norte");
        client.delete().uri("/api/productos/eliminar/" + a).exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.status").isEqualTo(200).jsonPath("$.data.delete").isEqualTo(true)
            .jsonPath("$.data.id").isEqualTo(a).jsonPath("$.data.nombre").isEqualTo("Nuevo A")
            .jsonPath("$.data.message").isEqualTo("Producto eliminado correctamente");
        client.get().uri("/api/productos/consultar/" + a).exchange().expectStatus().isNotFound().expectBody()
            .jsonPath("$.status").isEqualTo(404).jsonPath("$.data.message").isEqualTo("Producto no encontrado");
    }

    private void update(String path, Object body) {
        client.put().uri(path).bodyValue(body).exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.status").isEqualTo(200).jsonPath("$.data.update").isEqualTo(true)
            .jsonPath("$.data.id").isNumber().jsonPath("$.data.nombre").isNotEmpty()
            .jsonPath("$.data.message").isNotEmpty();
    }

    @Test
    void rejectsInvalidInputAndMissingParents() {
        client.post().uri("/api/franquicias/crear").bodyValue(Map.of("nombre", " ")).exchange()
            .expectStatus().isBadRequest().expectBody().jsonPath("$.status").isEqualTo(400);
        client.post().uri("/api/productos/crear").bodyValue(Map.of("nombre", "P", "stock", -1, "sucursal", Map.of("id", 1)))
            .exchange().expectStatus().isBadRequest().expectBody().jsonPath("$.status").isEqualTo(400);
        client.post().uri("/api/productos/crear").bodyValue(Map.of("nombre", "P", "sucursal", Map.of("id", 1)))
            .exchange().expectStatus().isBadRequest();
        client.post().uri("/api/sucursales/crear").bodyValue(Map.of("nombre", "S", "franquicia", Map.of("id", 999)))
            .exchange().expectStatus().isNotFound().expectBody().jsonPath("$.status").isEqualTo(404);
        client.post().uri("/api/productos/crear").bodyValue(Map.of("nombre", "P", "stock", 1, "sucursal", Map.of("id", 999)))
            .exchange().expectStatus().isNotFound();
        client.get().uri("/api/franquicias/consultar-mayor-stock/999").exchange().expectStatus().isNotFound();
        client.delete().uri("/api/productos/eliminar/999").exchange().expectStatus().isNotFound();
        client.put().uri("/api/productos/actualizar-stock/999").bodyValue(Map.of("stock", 2)).exchange().expectStatus().isNotFound();
        client.get().uri("/api/productos/consultar/no-numero").exchange().expectStatus().isBadRequest().expectBody()
            .jsonPath("$.status").isEqualTo(400);
        client.post().uri("/api/franquicias/crear").contentType(MediaType.APPLICATION_JSON).bodyValue("{malformed")
            .exchange().expectStatus().isBadRequest().expectBody().jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void emptyListAndUnknownRouteUseTheSameEnvelope() {
        long id = create("/api/franquicias/crear", Map.of("nombre", "Vacía"));
        client.get().uri("/api/franquicias/consultar-mayor-stock/" + id).exchange().expectStatus().isOk()
            .expectBody().jsonPath("$.status").isEqualTo(200).jsonPath("$.data").isEmpty();
        client.get().uri("/ruta-inexistente").exchange().expectStatus().isNotFound().expectBody()
            .jsonPath("$.status").isEqualTo(404).jsonPath("$.data.message").isEqualTo("Recurso no encontrado");
    }

    @Test
    void errorsAreSanitizedAndHttpStatusMatchesBody() {
        for (int code : new int[]{401, 403, 409, 500}) {
            client.get().uri("/test/status/" + code).exchange().expectStatus().isEqualTo(code).expectBody()
                .jsonPath("$.status").isEqualTo(code).jsonPath("$.data.message").isNotEmpty()
                .jsonPath("$.trace").doesNotExist()
                .consumeWith(result -> assertThat(new String(result.getResponseBody())).doesNotContain("secret-internal"));
        }
    }

    @Test
    void binaryAndBodylessResponsesAreNotWrapped() {
        client.get().uri("/test/file").exchange().expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .expectBody().consumeWith(result -> assertThat(result.getResponseBody()).containsExactly(1, 2, 3));
        client.get().uri("/test/empty").exchange().expectStatus().isNoContent().expectBody().isEmpty();
        client.get().uri("/test/wrapped").exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.status").isEqualTo(200).jsonPath("$.data.answer").isEqualTo(42)
            .jsonPath("$.data.status").doesNotExist();
    }

    @TestConfiguration
    static class TestEndpoints {
        @Bean FailureController failureController() { return new FailureController(); }
    }
    @RestController
    static class FailureController {
        @GetMapping("/test/status/{code}")
        Object fail(@PathVariable int code) {
            if (code == 500) throw new IllegalStateException("secret-internal");
            if (code == 409) throw new org.springframework.dao.DataIntegrityViolationException("secret-internal");
            throw new ResponseStatusException(HttpStatusCode.valueOf(code), "secret-internal");
        }
        @GetMapping("/test/file")
        ResponseEntity<ByteArrayResource> file() {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(new byte[]{1, 2, 3}));
        }
        @GetMapping("/test/empty") ResponseEntity<Void> empty() { return ResponseEntity.noContent().build(); }
        @GetMapping("/test/wrapped") ApiResponse<Map<String, Integer>> wrapped() { return new ApiResponse<>(200, Map.of("answer", 42)); }
    }
}
