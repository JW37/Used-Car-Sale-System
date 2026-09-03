package com.automart.service;

import com.automart.dto.CarResponse;
import com.automart.entity.Car;
import com.automart.entity.Favorite;
import com.automart.entity.User;
import com.automart.exception.BadRequestException;
import com.automart.exception.ResourceNotFoundException;
import com.automart.repository.CarRepository;
import com.automart.repository.FavoriteRepository;
import com.automart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarService carService;

    @Transactional
    public void addFavorite(Long userId, Long carId) {
        if (favoriteRepository.existsByUserIdAndCarId(userId, carId)) {
            throw new BadRequestException("Car is already in favorites");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        favoriteRepository.save(Favorite.builder().user(user).car(car).build());
    }

    @Transactional
    public void removeFavorite(Long userId, Long carId) {
        favoriteRepository.deleteByUserIdAndCarId(userId, carId);
    }

    public List<CarResponse> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(fav -> carService.getCarById(fav.getCar().getId()))
                .collect(Collectors.toList());
    }
}
