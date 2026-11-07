package com.api.franquicia.configuration;

import com.api.franquicia.repository.*;
import com.api.franquicia.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {
    @Bean FranquiciaService franquicias(FranquiciaRepository repository) { return new FranquiciaService(repository); }
    @Bean SucursalService sucursales(SucursalRepository repository, FranquiciaService franquicias) {
        return new SucursalService(repository, franquicias);
    }
    @Bean ProductoService productos(ProductoRepository repository, SucursalService sucursales, FranquiciaService franquicias) {
        return new ProductoService(repository, sucursales, franquicias);
    }
}
