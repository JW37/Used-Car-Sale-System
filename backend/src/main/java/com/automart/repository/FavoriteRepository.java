package com.automart.repository;

import com.automart.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndCarId(Long userId, Long carId);
    boolean existsByUserIdAndCarId(Long userId, Long carId);
    void deleteByUserIdAndCarId(Long userId, Long carId);
}
