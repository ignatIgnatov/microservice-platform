package com.platform.ads.repository;

import com.platform.ads.entity.Brand;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BrandRepository extends ReactiveCrudRepository<Brand, Long> {

    @Query("SELECT * FROM brands WHERE category = :category AND active = true ORDER BY display_order ASC, name ASC")
    Flux<Brand> findByCategoryOrderByDisplayOrder(String category);

    @Query("SELECT * FROM brands WHERE active = true ORDER BY category ASC, display_order ASC, name ASC")
    Flux<Brand> findAllActiveOrderByCategory();

    @Query("SELECT DISTINCT category FROM brands WHERE active = true ORDER BY category")
    Flux<String> findDistinctActiveCategories();

    @Query("SELECT * FROM brands WHERE LOWER(name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND active = true")
    Flux<Brand> searchByNameContaining(String searchTerm);

    @Query("SELECT * FROM brands WHERE category = :category AND LOWER(name) = LOWER(:name) AND active = true")
    Mono<Brand> findByCategoryAndNameIgnoreCase(String category, String name);

    @Query("SELECT COUNT(*) FROM brands WHERE category = :category AND active = true")
    Mono<Long> countByCategory(String category);
}