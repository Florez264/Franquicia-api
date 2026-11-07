package com.api.franquicia.infrastructure.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

interface FranquiciaCrudRepository extends ReactiveCrudRepository<FranquiciaRow, Long> {}
