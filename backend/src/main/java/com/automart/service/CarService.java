package com.automart.service;

import com.automart.dto.CarRequest;
import com.automart.dto.CarResponse;
import com.automart.entity.*;
import com.automart.exception.AccessDeniedCustomException;
import com.automart.exception.ResourceNotFoundException;
import com.automart.repository.CarImageRepository;
import com.automart.repository.CarRepository;
import com.automart.repository.UserRepository;
import com.automart.repository.spec.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final UserRepository userRepository;

    @Transactional
    public CarResponse createCar(CarRequest request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Car car = Car.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .price(request.getPrice())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .kilometers(request.getKilometers())
                .location(request.getLocation())
                .description(request.getDescription())
                .status(CarStatus.PENDING) // every new listing needs admin approval
                .seller(seller)
                .build();

        Car saved = carRepository.save(car);

        if (request.getImageUrls() != null) {
            List<CarImage> images = request.getImageUrls().stream()
                    .map(url -> CarImage.builder().car(saved).imageUrl(url).build())
                    .collect(Collectors.toList());
            carImageRepository.saveAll(images);
            saved.setImages(images);
        }

        return toResponse(saved);
    }

    @Transactional
    public CarResponse updateCar(Long carId, CarRequest request, Long requesterId, boolean isAdmin) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        // Ownership check: a seller may only edit their own listings;
        // an admin may edit any. This is exactly the kind of business
        // rule that belongs in the service layer, not just @PreAuthorize,
        // because it depends on the specific row's data (seller_id),
        // not just the caller's role.
        if (!isAdmin && !car.getSeller().getId().equals(requesterId)) {
            throw new AccessDeniedCustomException("You do not own this listing");
        }

        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setPrice(request.getPrice());
        car.setFuelType(request.getFuelType());
        car.setTransmission(request.getTransmission());
        car.setKilometers(request.getKilometers());
        car.setLocation(request.getLocation());
        car.setDescription(request.getDescription());
        // Editing content sends it back for re-approval so admins vet changes too.
        if (!isAdmin) {
            car.setStatus(CarStatus.PENDING);
        }

        // Replace the image set when the caller sends one. We clear the
        // existing collection rather than diffing it — simpler, and safe
        // here because CarImage rows have no other data worth preserving.
        // orphanRemoval = true on Car.images means the cleared-out rows
        // are deleted from car_images automatically on save.
        if (request.getImageUrls() != null) {
            car.getImages().clear();
            List<CarImage> newImages = request.getImageUrls().stream()
                    .map(url -> CarImage.builder().car(car).imageUrl(url).build())
                    .collect(Collectors.toList());
            car.getImages().addAll(newImages);
        }

        return toResponse(carRepository.save(car));
    }

    @Transactional
    public void deleteCar(Long carId, Long requesterId, boolean isAdmin) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        if (!isAdmin && !car.getSeller().getId().equals(requesterId)) {
            throw new AccessDeniedCustomException("You do not own this listing");
        }
        carRepository.delete(car); // cascades to CarImage via CascadeType.ALL
    }

    @Transactional
    public CarResponse markSold(Long carId, Long requesterId, boolean isAdmin) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        if (!isAdmin && !car.getSeller().getId().equals(requesterId)) {
            throw new AccessDeniedCustomException("You do not own this listing");
        }
        car.setStatus(CarStatus.SOLD);
        return toResponse(carRepository.save(car));
    }

    public CarResponse getCarById(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        return toResponse(car);
    }

    // Public marketplace listing: only APPROVED cars, paginated.
    public Page<CarResponse> getApprovedCars(Pageable pageable) {
        Specification<Car> spec = CarSpecification.hasStatus(CarStatus.APPROVED);
        return carRepository.findAll(spec, pageable).map(this::toResponse);
    }

    // Combines every optional filter into one dynamic query via Specification.
    public Page<CarResponse> search(String keyword, String brand, BigDecimal minPrice, BigDecimal maxPrice,
                                     FuelType fuelType, Integer year, String location, Pageable pageable) {
        Specification<Car> spec = Specification
                .where(CarSpecification.hasStatus(CarStatus.APPROVED))
                .and(CarSpecification.keyword(keyword))
                .and(CarSpecification.hasBrand(brand))
                .and(CarSpecification.priceBetween(minPrice, maxPrice))
                .and(CarSpecification.hasFuelType(fuelType))
                .and(CarSpecification.hasYear(year))
                .and(CarSpecification.hasLocation(location));

        return carRepository.findAll(spec, pageable).map(this::toResponse);
    }

    public List<CarResponse> getMyListings(Long sellerId) {
        Specification<Car> spec = (root, query, cb) -> cb.equal(root.get("seller").get("id"), sellerId);
        return carRepository.findAll(spec).stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ---- Admin operations ----

    public List<CarResponse> getPendingCars() {
        Specification<Car> spec = CarSpecification.hasStatus(CarStatus.PENDING);
        return carRepository.findAll(spec).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CarResponse approveCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        car.setStatus(CarStatus.APPROVED);
        return toResponse(carRepository.save(car));
    }

    @Transactional
    public CarResponse rejectCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        car.setStatus(CarStatus.REJECTED);
        return toResponse(carRepository.save(car));
    }

    private CarResponse toResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .price(car.getPrice())
                .fuelType(car.getFuelType())
                .transmission(car.getTransmission())
                .kilometers(car.getKilometers())
                .location(car.getLocation())
                .description(car.getDescription())
                .status(car.getStatus())
                .sellerId(car.getSeller().getId())
                .sellerName(car.getSeller().getName())
                .imageUrls(car.getImages().stream().map(CarImage::getImageUrl).collect(Collectors.toList()))
                .createdAt(car.getCreatedAt())
                .build();
    }
}
