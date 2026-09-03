package com.automart.controller;

import com.automart.dto.CarRequest;
import com.automart.dto.CarResponse;
import com.automart.entity.FuelType;
import com.automart.security.UserPrincipal;
import com.automart.service.CarService;
import com.automart.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final FileStorageService fileStorageService;

    // Public marketplace listing — only APPROVED cars, paginated.
    // Spring auto-binds ?page=0&size=12&sort=price,desc into Pageable.
    @GetMapping
    public ResponseEntity<Page<CarResponse>> getAllCars(Pageable pageable) {
        return ResponseEntity.ok(carService.getApprovedCars(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CarResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String location,
            Pageable pageable) {
        return ResponseEntity.ok(
                carService.search(keyword, brand, minPrice, maxPrice, fuelType, year, location, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCar(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping("/my-listings")
    public ResponseEntity<List<CarResponse>> myListings(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(carService.getMyListings(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        CarResponse response = carService.createCar(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(@PathVariable Long id,
                                                  @Valid @RequestBody CarRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole().name().equals("ADMIN");
        return ResponseEntity.ok(carService.updateCar(id, request, principal.getId(), isAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole().name().equals("ADMIN");
        carService.deleteCar(id, principal.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<CarResponse> markSold(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole().name().equals("ADMIN");
        return ResponseEntity.ok(carService.markSold(id, principal.getId(), isAdmin));
    }

    // Separate multipart endpoint: keeps CarRequest a clean JSON DTO instead
    // of mixing JSON fields and file parts in one request body.
    @PostMapping("/{id}/images")
    public ResponseEntity<Map<String, String>> uploadImage(@PathVariable Long id,
                                                             @RequestParam("file") MultipartFile file) {
        String url = fileStorageService.store(file);
        return ResponseEntity.ok(Map.of("imageUrl", url));
    }
}
