package com.platform.ads.repository;

import com.platform.ads.entity.BoatExteriorFeature;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoatExteriorFeatureRepository extends ReactiveCrudRepository<BoatExteriorFeature, Long> {
    Flux<BoatExteriorFeature> findByBoatSpecId(Long boatSpecId);

    Mono<Void> deleteByBoatSpecId(Long boatSpecId);
}
