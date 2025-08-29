package com.platform.ads.repository;

import com.platform.ads.entity.EngineSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EngineSpecificationRepository extends ReactiveCrudRepository<EngineSpecification, Long> {

    Mono<EngineSpecification> findByAdId(Long adId);

    Flux<EngineSpecification> findByBrandIgnoreCaseContaining(String brand);

    @Query("SELECT * FROM engine_specifications es JOIN ads a ON es.ad_id = a.id WHERE " +
            "(:engineType IS NULL OR es.engine_type = :engineType) AND " +
            "(:brand IS NULL OR LOWER(es.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:strokeType IS NULL OR es.stroke_type = :strokeType) AND " +
            "(:minHorsepower IS NULL OR es.horsepower >= :minHorsepower) AND " +
            "(:maxHorsepower IS NULL OR es.horsepower <= :maxHorsepower) AND " +
            "(:minYear IS NULL OR es.year >= :minYear) AND " +
            "(:maxYear IS NULL OR es.year <= :maxYear) AND " +
            "(:fuelType IS NULL OR es.fuel_type = :fuelType) AND " +
            "(:ignitionType IS NULL OR es.ignition_type = :ignitionType) AND " +
            "(:controlType IS NULL OR es.control_type = :controlType) AND " +
            "(:shaftLength IS NULL OR es.shaft_length = :shaftLength) AND " +
            "(:color IS NULL OR es.color = :color) AND " +
            "(:inWarranty IS NULL OR es.in_warranty = :inWarranty) AND " +
            "(:condition IS NULL OR es.condition = :condition) AND " +
            "a.active = true")
    Flux<EngineSpecification> searchEngines(String engineType, String brand, String strokeType,
                                            Integer minHorsepower, Integer maxHorsepower,
                                            Integer minYear, Integer maxYear,
                                            String fuelType, String ignitionType,
                                            String controlType, String shaftLength,
                                            String color, Boolean inWarranty, String condition);
}
