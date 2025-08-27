package com.platform.ads.repository;

import com.platform.ads.entity.Ad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    Page<Ad> findByUserId(String userId, Pageable pageable);
    Page<Ad> findByCategory(String category, Pageable pageable);
    Page<Ad> findByStatus(Ad.AdStatus status, Pageable pageable);
}