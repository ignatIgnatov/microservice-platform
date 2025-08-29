package com.platform.ads.repository;

import com.platform.ads.entity.BoatEquipment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoatEquipmentRepository extends ReactiveCrudRepository<BoatEquipment, Long> {
    Flux<BoatEquipment> findByBoatSpecId(Long boatSpecId);

    Mono<Void> deleteByBoatSpecId(Long boatSpecId);
}
