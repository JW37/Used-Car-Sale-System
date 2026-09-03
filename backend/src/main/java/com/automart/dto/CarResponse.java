package com.automart.dto;

import com.automart.entity.CarStatus;
import com.automart.entity.FuelType;
import com.automart.entity.Transmission;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// We NEVER return the Car entity directly from a controller.
// Returning entities leaks lazy-loading proxies, can trigger
// LazyInitializationException, and couples the API to the DB schema.
// A response DTO gives full control over the JSON shape.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal price;
    private FuelType fuelType;
    private Transmission transmission;
    private Integer kilometers;
    private String location;
    private String description;
    private CarStatus status;
    private Long sellerId;
    private String sellerName;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
