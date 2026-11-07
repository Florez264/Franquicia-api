package com.api.franquicia.infrastructure.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

interface SucursalCrudRepository extends ReactiveCrudRepository<SucursalRow, Long> {}
