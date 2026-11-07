package com.api.franquicia;

import com.api.franquicia.model.*;
import com.api.franquicia.repository.*;
import com.api.franquicia.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceTest {
    FranquiciaRepository franchises = mock(FranquiciaRepository.class);
    SucursalRepository branches = mock(SucursalRepository.class);
    ProductoRepository products = mock(ProductoRepository.class);
    FranquiciaService fs = new FranquiciaService(franchises);
    SucursalService ss = new SucursalService(branches, fs);
    ProductoService ps = new ProductoService(products, ss, fs);

    @Test void createIsLazyAndDoesNotPersistBeforeSubscription() {
        Franquicia value = new Franquicia(1L, "Centro");
        when(franchises.save(new Franquicia(null, "Centro"))).thenReturn(Mono.just(value));
        Mono<Franquicia> operation = fs.crearFranquicia("Centro");
        verifyNoInteractions(franchises);
        StepVerifier.create(operation).expectNext(value).verifyComplete();
    }

    @Test void missingParentPreventsSavingChild() {
        when(franchises.findById(8L)).thenReturn(Mono.empty());
        StepVerifier.create(ss.agregarSucursal("S", 8L)).expectError(RecursoNoEncontradoException.class).verify();
        verifyNoInteractions(branches);
        when(branches.findById(9L)).thenReturn(Mono.empty());
        StepVerifier.create(ps.agregarProducto("P", 4, 9L)).expectError(RecursoNoEncontradoException.class).verify();
        verifyNoInteractions(products);
    }

    @Test void stockUpdatePreservesProductIdentityNameAndBranch() {
        Producto old = new Producto(2L, "P", 3, 7L);
        Producto updated = new Producto(2L, "P", 10, 7L);
        when(products.findById(2L)).thenReturn(Mono.just(old));
        when(products.save(updated)).thenReturn(Mono.just(updated));
        StepVerifier.create(ps.modificarStock(2L, 10)).expectNext(updated).verifyComplete();
        verify(products).save(updated);
    }

    @Test void invalidStockNeverReachesPersistence() {
        when(products.findById(2L)).thenReturn(Mono.just(new Producto(2L, "P", 3, 7L)));
        StepVerifier.create(ps.modificarStock(2L, -1)).expectError(IllegalArgumentException.class).verify();
        verify(products, never()).save(any());
    }

    @Test void maximumQueryRequiresExistingFranchise() {
        when(franchises.findById(99L)).thenReturn(Mono.empty());
        StepVerifier.create(ps.mayoresPorSucursal(99L)).expectError(RecursoNoEncontradoException.class).verify();
        verifyNoInteractions(products);
    }

    @Test void objectRulesApplyOutsideHttpLayer() {
        assertThatThrownBy(() -> new Franquicia(null, " ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Producto(null, "P", -1, 1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Sucursal(null, "S", null)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new Franquicia(1L, " Centro ").nombre()).isEqualTo("Centro");
    }
}
