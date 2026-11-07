package com.api.franquicia;

import com.api.franquicia.controller.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

class ApiResponsesTest {
    @Test void retainsListPaginationMetadataWithoutFlattening() {
        var page = Map.of("content", List.of("one"), "totalElements", 12, "number", 0, "size", 1);
        var response = ApiResponses.ok(page);
        assertThat(response.getStatusCode().value()).isEqualTo(response.getBody().status());
        assertThat(response.getBody().data()).isSameAs(page);
    }
    @Test void rejectsDoubleWrappingAndBodylessStatus() {
        assertThatThrownBy(() -> ApiResponses.ok(new ApiResponse<>(200, "x"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ApiResponses.of(HttpStatus.NO_CONTENT, "x")).isInstanceOf(IllegalArgumentException.class);
    }
}
