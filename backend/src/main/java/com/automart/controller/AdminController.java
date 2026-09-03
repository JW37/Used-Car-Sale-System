package com.automart.controller;

import com.automart.dto.CarResponse;
import com.automart.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Everything under /api/admin/** is locked to ROLE_ADMIN in SecurityConfig,
// so no per-method @PreAuthorize is needed here — but you could add
// @PreAuthorize("hasRole('ADMIN')") too for defense-in-depth / clarity.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CarService carService;

    @GetMapping("/cars/pending")
    public ResponseEntity<List<CarResponse>> pendingCars() {
        return ResponseEntity.ok(carService.getPendingCars());
    }

    @PatchMapping("/cars/{id}/approve")
    public ResponseEntity<CarResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(carService.approveCar(id));
    }

    @PatchMapping("/cars/{id}/reject")
    public ResponseEntity<CarResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(carService.rejectCar(id));
    }
}
