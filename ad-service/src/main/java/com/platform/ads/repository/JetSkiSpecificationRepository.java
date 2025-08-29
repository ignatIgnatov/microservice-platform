package com.platform.ads.repository;

import com.platform.ads.entity.JetSkiSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface JetSkiSpecificationRepository extends ReactiveCrudRepository<JetSkiSpecification, Long> {

    Mono<JetSkiSpecification> findByAdId(Long adId);

    Flux<JetSkiSpecification> findByBrandIgnoreCaseContaining(String brand);

    @Query("SELECT * FROM jetski_specifications js JOIN ads a ON js.ad_id = a.id WHERE " +
            "(:brand IS NULL OR LOWER(js.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:model IS NULL OR LOWER(js.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
            "(:minHorsepower IS NULL OR js.horsepower >= :minHorsepower) AND " +
            "(:maxHorsepower IS NULL OR js.horsepower <= :maxHorsepower) AND " +
            "(:minYear IS NULL OR js.year >= :minYear) AND " +
            "(:maxYear IS NULL OR js.year <= :maxYear) AND " +
            "(:fuelType IS NULL OR js.fuel_type = :fuelType) AND " +
            "(:trailerIncluded IS NULL OR js.trailer_included = :trailerIncluded) AND " +
            "(:inWarranty IS NULL OR js.in_warranty = :inWarranty) AND " +
            "(:isRegistered IS NULL OR js.is_registered = :isRegistered) AND " +
            "(:condition IS NULL OR js.condition = :condition) AND " +
            "a.active = true")
    Flux<JetSkiSpecification> searchJetSkis(String brand, String model,
                                            Integer minHorsepower, Integer maxHorsepower,
                                            Integer minYear, Integer maxYear,
                                            String fuelType, Boolean trailerIncluded,
                                            Boolean inWarranty, Boolean isRegistered,
                                            String condition);
}
