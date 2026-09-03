package com.automart.dto;

import com.automart.entity.FuelType;
import com.automart.entity.Transmission;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

// Used for both create and update of a car listing.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CarRequest {

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    @Min(1980)
    private Integer year;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull
    private FuelType fuelType;

    @NotNull
    private Transmission transmission;

    @NotNull
    @Min(0)
    private Integer kilometers;

    @NotBlank
    private String location;

    private String description;

    // plain image URLs (from the /api/cars/{id}/images upload endpoint,
    // or external links) — kept simple for a portfolio project
    private List<String> imageUrls;
}
