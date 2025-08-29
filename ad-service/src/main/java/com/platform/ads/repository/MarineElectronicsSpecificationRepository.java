package com.platform.ads.repository;

import com.platform.ads.entity.MarineElectronicsSpecification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MarineElectronicsSpecificationRepository extends ReactiveCrudRepository<MarineElectronicsSpecification, Long> {
    Mono<MarineElectronicsSpecification> findByAdId(Long adId);
    Mono<Void> deleteByAdId(Long adId);
}
